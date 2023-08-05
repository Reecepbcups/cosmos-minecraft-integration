<script lang="ts">
	import type { OfflineAminoSigner } from '@cosmjs/amino';
	import type { OfflineDirectSigner } from '@cosmjs/proto-signing';
	
	import { SigningStargateClient } from '@cosmjs/stargate';

	import type { EncodeObject } from '@cosmjs/proto-signing';

	import type { Window as KeplrWindow } from '@keplr-wallet/types';

	// notifications
	import toast, { Toaster, type ToastOptions } from 'svelte-french-toast';

	// TODO: Future switch to a cosmwasm client
	import { getSigningCosmosClient } from 'juno-network';

	const toast_style: ToastOptions = {
		position: 'top-right',
		duration: 6000,
		style: 'background: #333; color: #fff; width: 15%; font-size: 1.1rem;'
	};

	const rpcEndpoint = 'https://uni-rpc.reece.sh/';
	const ENDPOINT = 'http://5.161.42.109:4000';
	const CHAIN_ID = 'uni-6';
  let fee = { amount: [{ amount: '500', denom: 'ujunox' }], gas: '250000' }; 

	let pendingTxs: Tx[] = [];

	let userAddress: string;
	const get_wallet_for_chain = async (
		chain_id: string
	): Promise<OfflineAminoSigner | OfflineDirectSigner> => {
		// open keplr
		const keplr = window as KeplrWindow;
		if (keplr === undefined) {
			toast.error(`Keplr not found`, toast_style);
			throw new Error('Keplr not found');
		}

		let signer = keplr.getOfflineSignerAuto;
		if (signer === undefined) {
			throw new Error('Keplr not found');
		}

		let s = signer(chain_id);
		return s;
	};

	// write a function which post a request
	async function doSignature(txId: string, tx: Tx) {
		// const url = "";
		// const data = {
		//   tx: txId,
		//   hash: tendermintHash,
		// };

		// sign the tx and broadcast

    // log tx
    console.log(tx);

		const signer = await get_wallet_for_chain(CHAIN_ID);
		// const client = await getSigningCosmosClient({ rpcEndpoint, signer });      
		const client = await SigningStargateClient.connectWithSigner(rpcEndpoint, signer); 

		// const txId = tx[0];
		// const txData = tx[1];

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
			toast.error(`Error: ${res.rawLog}`, toast_style);
		} else {
			toast.success(`Success: ${res.rawLog}`, toast_style);
			toast.success(`Success: ${res.transactionHash}`, toast_style);

			const url = `${ENDPOINT}/v1/tx/sign/${txId}/${res.transactionHash}`;
			console.log(url);
			
			// request.post(
			// 	"https://api.crafteconomy.io/v1/tx/sign/" + txID + "/" + tendermintHash,
			// 	{ json: { key: 'value' } },
			// 	function (error, response, body) {
			// 		if (!error && response.statusCode == 200) {
			// 			console.log(body);
			// 			window.location.reload();
			// 		}
			// 	}
			// );  

			// do the above in typescript
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
					// window.location.reload();
				})
				.catch((error) => {
					console.log(error);
				});
		}
	}

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

	function getPending(walletAddr: string): Tx[] {
		// http://5.161.42.109:4000/v1/tx/all/juno10r39fueph9fq7a6lgswu4zdsg8t3gxlq670lt0
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
				console.log(data);
				pendingTxs = data as Tx[];
				return pendingTxs;
			})
			.catch((error) => {
				console.log(error);
				pendingTxs = [];
				return [];
			});

		return pendingTxs;
	}
</script>

<main>  
  <h1>Pending Transactions</h1>
  <!-- {#if userAddress}
    <h3 style="margin-left:10px">{userAddress}</h3>
	{/if} -->



	<!-- create a connect wallet button -->
  <!-- put this bottn nex to pending Txs -->
	<button
    style="margin-left:10px"
		on:click={async () => {
			const signer = await get_wallet_for_chain(CHAIN_ID);
			userAddress = (await signer.getAccounts())[0].address;

			getPending(userAddress);
		}}>Connect Wallet</button
	>
	
	

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
