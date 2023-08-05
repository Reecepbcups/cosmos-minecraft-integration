<script lang="ts">
	import type { Window as KeplrWindow } from '@keplr-wallet/types';		
	import type { OfflineAminoSigner } from '@cosmjs/amino';
	import type { OfflineDirectSigner, EncodeObject } from '@cosmjs/proto-signing';	
	import { SigningStargateClient } from '@cosmjs/stargate';

	import { success_notification, error_notification } from './Status.svelte';

	// == Configuration ==
	export let ENDPOINT: string;
	export let CHAIN_ID: string;
	export let rpcEndpoint: string;

	let fee = { amount: [{ amount: '500', denom: 'ujunox' }], gas: '250000' };

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

	// Application Logic

	let userAddress: string;
	let pendingTxs: Tx[] = [];

	const get_wallet_for_chain = async (
		chain_id: string
	): Promise<OfflineAminoSigner | OfflineDirectSigner> => {
		// open keplr
		const keplr = window as KeplrWindow;
		if (keplr === undefined) {
			error_notification('Keplr not found');
			throw new Error('Keplr not found');
		}

		let signer = keplr.getOfflineSignerAuto;
		if (signer === undefined) {
			throw new Error('Keplr not found');
		}

		let s = signer(chain_id);
		return s;
	};

	async function doSignature(txId: string, tx: Tx) {
		console.log(tx);

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

		const res = await client.signAndBroadcast(userAddress, msgs, fee, tx.description);

		if (res !== undefined && res.code !== 0) {			
			error_notification(`Error: ${res.rawLog}`)
		} else {
			success_notification(`Success: ${res.rawLog}`);
			success_notification(`Success: ${res.transactionHash}`);

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
					success_notification(data);
				})
				.catch((error) => {
					error_notification(error);
				});
		}
	}

	function getPending(walletAddr: string): Tx[] {		
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
</script>

<main>
	<h1>Pending Transactions</h1>
	
	<button
		style="margin-left:10px"
		on:click={async () => {
			const signer = await get_wallet_for_chain(CHAIN_ID);
			userAddress = (await signer.getAccounts())[0].address;

			getPending(userAddress);
		}}>Connect Wallet</button
	>
	
	{#if pendingTxs.length === 0}
		<h2>No Pending Transactions</h2>
	{/if}

	<!-- TODO: make these drop down boxes -->
	<ul>
		{#each Object.entries(pendingTxs) as tx}
			<div class="container">
				<!-- no style for  -->
				<ul style="list-style-type: none;">
					<!-- {#each Object.entries(tx[1]) as txData}
					<li>{txData[0]}: {txData[1]}</li>
				{/each} -->
					<!-- <li>Server: {tx[1].server_name}</li> -->
					<li>Description: {tx[1].description}</li>
					<li>Amount: {tx[1].amount} {tx[1].denom}</li>
					<!-- if tx[1].tax.amount -->
					{#if tx[1].tax.amount > 0}
						<li>Tax: {tx[1].tax.amount} {tx[1].denom}</li>
					{/if}
					<button on:click={() => doSignature(tx[0], tx[1])}>Sign {tx[0]}</button>
				</ul>
			</div>
		{/each}
	</ul>
</main>

<style>
	button {
		border-radius: 10px;
		font-weight: bold;
		padding: 5px;
		border: 1px solid black;
	}

	.container {
		/* box has a thin black border, some box shadow and is a ligher shade of gray*/
		border: 1px solid black;
		box-shadow: 0 0 8px 0 rgba(0, 0, 0, 0.2);
		background-color: #f1f1f1;
		/* box is as wide as the content up to 50% */
		width: 50%;
		/* text color is black */
		color: black;
		/* hide ul marks */
		list-style-type: none;
		/* padding around the box */
		padding: 10px;
	}
</style>
