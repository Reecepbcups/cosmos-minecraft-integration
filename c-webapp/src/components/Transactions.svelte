<script lang="ts">
	import { AccordionItem, Accordion } from 'flowbite-svelte';
	import { Spinner } from 'flowbite-svelte';

	import { get_wallet_for_chain } from '../lib/wallet';

	import type { EncodeObject } from '@cosmjs/proto-signing';
	import { SigningStargateClient, type DeliverTxResponse } from '@cosmjs/stargate';

	import { ENDPOINT, DENOM, FEE_AMT, GAS_AMT, CHAIN_ID, rpcEndpoint, EXPLORER_TX } from '../lib/index';
	import { error_notification, success_notification } from '$lib/notifications';

	// == Configuration ==
	let isIntervalRunning = false;	
	const delay = 6; // seconds
	let secondsTillRefresh = delay;

	let fee = { amount: [{ amount: FEE_AMT, denom: DENOM }], gas: GAS_AMT };

	// == Types==
	interface Tx {
		server_name: string;
		amount: string;
		description: string;
		to_address: string;
		tax: {
			amount: number;
			address: string;
		};
		denom: string;
		tx_type: string;
		from_address: string;
		timestamp: number;
	}

	let userAddress: string;
	let pendingTxs: Tx[] = [];

	const denomToName = (denom: string): string => {
		return denom.substring(1, denom.length).toUpperCase();
	};

	const cleanDescription = (description: string): string => {
		const time_ = description.indexOf('time_');
		if (time_ > 0) {
			return description.substring(0, time_);
		}

		return description;
	};

	// let startSpinning = false;
	// let spinnerIdx = 0;
	async function doSignature(txId: string, tx: Tx) {
		console.log(tx);
		// startSpinning = true;

		const signer = await get_wallet_for_chain(CHAIN_ID);
		const client = await SigningStargateClient.connectWithSigner(rpcEndpoint, signer);

		let msgs: EncodeObject[] = [
			{
				typeUrl: '/cosmos.bank.v1beta1.MsgSend',
				value: {
					fromAddress: tx.from_address,
					toAddress: tx.to_address,
					amount: [
						{
							denom: tx.denom,
							amount: tx.amount
						}
					]
				}
			}
		];

		if (tx.tax.amount > 0) {
			msgs.push({
				typeUrl: '/cosmos.bank.v1beta1.MsgSend',
				value: {
					fromAddress: tx.from_address,
					toAddress: tx.tax.address,
					amount: [
						{
							denom: tx.denom,
							amount: tx.tax.amount.toString()
						}
					]
				}
			});
		}

		let res: DeliverTxResponse;
		try {
			res = await client.signAndBroadcast(userAddress, msgs, fee, tx.description);
		} catch (error) {
			error_notification(`Error: signing request rejected (keplr closed)`);
			// startSpinning = false;
			return;
		}

		if (res !== undefined && res.code !== 0) {
			error_notification(`Error: ${res.rawLog}`);
		} else {
			const url = `${ENDPOINT}/v1/tx/sign/${txId}/${res.transactionHash}`;
			const otherParams = {
				headers: {
					'content-type': 'application/json; charset=UTF-8'
				},
				method: 'POST'
			};

			fetch(url, otherParams)
				.then((response) => response.json())
				.then((data) => {
					console.log(data);
					success_notification(
						`Success: <a href="${EXPLORER_TX}/${res.transactionHash}" target="_blank"><u>Mintscan.io view</u></a>`,
						10_000
					);
					// startSpinning = false;
					// spinnerIdx = 0;
					getPending(userAddress);
				})
				.catch((error) => {
					error_notification(error);
					// startSpinning = false;
					// spinnerIdx = 0;
					getPending(userAddress);
				});
		}
	}

	function getPending(walletAddr: string): Tx[] {
		if (walletAddr === undefined || walletAddr === '') {
			return [];
		}

		const url = ENDPOINT + '/v1/tx/all/' + walletAddr;
		const otherParams = {
			headers: {
				'content-type': 'application/json; charset=UTF-8'
			},
			method: 'GET'
		};

		fetch(url, otherParams)
			.then((response) => response.json())
			.then((data) => {
				console.log('Found Txs', data);

				pendingTxs = data as Tx[];
				return pendingTxs;
			})
			.catch((error) => {
				console.log(error);
				error_notification(error);
				pendingTxs = [];
				return [];
			});

		return pendingTxs;
	}

	let searchString = '';
</script>

<main>
	<Accordion class="mt-8 md:mt-0 md:col-span-3 md:ml-32 md:mr-32 md:mt-4 md:mb-4">
		<div class="flex justify-between">
			<h1 class="text-4xl font-bold text-gray-900 dark:text-white mx-auto mt-20">Transactions</h1>
			<!-- {#if startSpinning} -->
			<!-- {/if} -->								
		</div>

		<!-- if userAddress is not set, show the button -->
		{#if userAddress === undefined || userAddress === ''}
			<!-- connect button which is in the center of the screen, blue -->
			<center>
				<button
					class="bg-blue-500 hover:bg-blue-400 text-white font-bold rounded py-3 px-20 mt-16"
					on:click={async () => {
						const signer = await get_wallet_for_chain(CHAIN_ID);
						userAddress = (await signer.getAccounts())[0].address;

						getPending(userAddress);
						setInterval(() => {
							if(secondsTillRefresh <= 0) {
								getPending(userAddress);
								isIntervalRunning = true;							
								secondsTillRefresh = delay+1;				
							}

							secondsTillRefresh--;
						}, 1_000);
					}}>Connect Wallet</button
				>
			</center>
		{:else}

			<center>
				<p class="text-white text-sm mt-4">Auto refresh in {secondsTillRefresh} seconds</p>
			</center>

			{#if Object.entries(pendingTxs).length == 0}
				<center>
					<p class="text-white text-sm mt-4">No pending transactions for {userAddress}</p>
				</center>
			<!-- {:else}				
				<form>   
					<label for="search" class="mb-2 text-sm font-medium text-gray-900 sr-only dark:text-white">Search</label>
					<div class="relative">
						<div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
							<svg class="w-4 h-4 text-gray-500 dark:text-gray-400" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
								<path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"/>
							</svg>
						</div>
						<input bind:value={searchString} type="search" id="search" class="block w-full p-4 pl-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="Search" required>
						<button type="submit" class="text-white absolute right-2.5 bottom-2.5 bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-4 py-2 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">Search</button>
					</div>
				</form>	 -->
			{/if}
		{/if}



		

		{#each Object.entries(pendingTxs) as tx, idx}

			<!-- make it 60% of the screen -->
			<AccordionItem class="rounded-lg mb-4">
				<span slot="header" class="text-base flex gap-2 text-white">
					<svg
						xmlns="http://www.w3.org/2000/svg"
						fill="none"
						viewBox="0 0 24 24"
						stroke-width="1.5"
						stroke="currentColor"
						class="w-6 h-6"
						><path
							stroke-linecap="round"
							stroke-linejoin="round"
							d="M20.25 7.5l-.625 10.632a2.25 2.25 0 01-2.247 2.118H6.622a2.25 2.25 0 01-2.247-2.118L3.75 7.5M10 11.25h4M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125z"
						/></svg
					>
					<span
						>Type: {tx[1].tx_type} | Cost: {Number(tx[1].amount) / 1_000_000} {denomToName(tx[1].denom)} | {cleanDescription(tx[1].description)}</span>

					<!-- {#if isIntervalRunning && spinnerIdx === idx}
						<div class="text-left"><Spinner color="green"/></div>
					{/if} -->
				</span>

				<span>
					<button
						on:click={() => {
							doSignature(tx[0], tx[1]);
							// spinnerIdx=idx
						}}
						class="bg-blue-500 hover:bg-blue-400 text-white font-bold rounded float-right py-2 px-8"
					>
						Sign Transaction
					</button>

					<ul class="list-disc list-inside">
						<li>ID: {tx[0]}</li>
						<li>Type: {tx[1].tx_type}</li>
						<li>Info: {cleanDescription(tx[1].description)}</li>
						<li>From: {tx[1].from_address}</li>
						<li>To: {tx[1].to_address}</li>
						<!-- if tx[1].tax > 0 -->
						{#if tx[1].tax.amount > 0}
							<p class="mb-2 text-gray-500 dark:text-gray-400">
								<!-- Tax: {tx[1].tax.amount}
								{denomToName(tx[1].denom)} -->
								<li>Tax: {tx[1].tax.amount/1_000_000} {denomToName(tx[1].denom)} ({(tx[1].tax.amount)/Number(tx[1].amount)*100}%) </li>
							</p>
						{/if}	
					</ul>

									
				</span>
			</AccordionItem>
		{/each}
	</Accordion>
</main>
