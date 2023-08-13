<script lang="ts">	
	import {get_wallet_for_chain} from '../lib/wallet';

	import { success_notification, error_notification } from '../lib/notifications';
	import { FAUCET, CHAIN_ID } from '../lib/index';	
	
	async function doFaucet(keplrWallet: string) {
		console.log(keplrWallet);

		if (keplrWallet.length == 0) {			
			error_notification("Keplr Wallet must be approved to link your accounts");
			return;
		}

		const url = `${FAUCET}/${CHAIN_ID}/` + keplrWallet;
		const otherParams = {
			headers: {
				'content-type': 'application/json; charset=UTF-8'
			},
			method: 'GET'
		};
		
		fetch(url, {
			...otherParams,			
		})
			.then((response) => response.json())
			.then((data) => {
				console.log(data);	
	
				if(data.error) {
					error_notification(data.error);
					return;
				}
				
				success_notification(data.message);
			})
			.catch((err) => {
				console.log(err);				
				error_notification(`Error: ${err.error}`);
			});
	}
</script>

<main>
	<div class="flex flex-col justify-center items-center mt-32">		
		<h1 class="text-4xl font-bold text-gray-900 dark:text-white">Juno Faucet</h1>
		<hr class="w-1/2 mt-2 mb-2 border-gray-300 dark:border-gray-600" />
		
		<h3 class="text-xl font-bold text-gray-900 dark:text-white mt-8">Wallet Connect</h3>

		<!-- if FAUCET is 'Faucet Is Disabled', show FAUCET -->
		{#if FAUCET == 'Faucet Is Disabled'}
			<p class="text-gray-900 dark:text-white mt-4">Faucet is disabled since this app is on mainnet.</p>

			<!-- a button which when pressed opens https://app.osmosis.zone/?from=USDC&to=JUNO -->
			<a
				href="https://app.osmosis.zone/?from=USDC&to=JUNO"
				class="bg-blue-500 hover:bg-blue-400 text-white font-bold rounded py-2 px-8 mt-8"
				target="_blank"
				rel="noopener noreferrer">Get some JUNO on Osmosis DEX</a>			

		{:else}
			<button
				class="bg-blue-500 hover:bg-blue-400 text-white font-bold rounded py-2 px-8 mt-8"
				on:click={async () => {
					const signer = await get_wallet_for_chain(CHAIN_ID);
					const keplrWallet = (await signer.getAccounts())[0].address;

					doFaucet(keplrWallet);
				}}>Receive some funds on {CHAIN_ID}</button
			>
		{/if}	
	</div>
</main>
