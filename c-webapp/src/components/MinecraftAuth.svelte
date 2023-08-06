<script lang="ts">	
	import {get_wallet_for_chain} from '../lib/wallet';

	import { success_notification, error_notification } from '../lib/notifications';
	import { ENDPOINT, CHAIN_ID } from '../lib/index';

	// Application Logic
	let userAddress: string;

	async function doConnection(keplrWallet: string, minecraftCode: string) {
		console.log(keplrWallet, minecraftCode);
		
		if (minecraftCode.length == 0) {			
			error_notification('Minecraft Code cannot be empty');			
			return;
		}

		if (keplrWallet.length == 0) {			
			error_notification("Keplr Wallet must be approved to link your accounts");
			return;
		}

		const url = `${ENDPOINT}/v1/connections/link`;
		const otherParams = {
			headers: {
				'content-type': 'application/json; charset=UTF-8'
			},
			method: 'POST'
		};
		const payload = {
			keplrId: keplrWallet,
			minecraftCode: minecraftCode
		};
		
		fetch(url, {
			...otherParams,
			body: JSON.stringify(payload)
		})
			.then((response) => response.json())
			.then((data) => {
				console.log(data);				

				if (JSON.stringify(data).includes('not found')) {					
					error_notification(data);
					return;
				}
				
				success_notification(`Linked your Minecraft account -> Keplr Wallet`);
			})
			.catch((err) => {
				console.log(err);				
				error_notification(`Error: ${err}`);
			});
	}

	let inputCode = '';
</script>

<main>
	<div class="flex flex-col justify-center items-center mt-32">		
		<h1 class="text-4xl font-bold text-gray-900 dark:text-white">Authentication</h1>
		<hr class="w-1/2 mt-2 mb-2 border-gray-300 dark:border-gray-600" />
		
		<h3 class="text-xl font-bold text-gray-900 dark:text-white mt-8">Minecraft Code</h3>

		<div class="flex mt-8">
			<span
				class="inline-flex items-center px-3 text-sm text-gray-900 bg-gray-200 border border-r-0 border-gray-300 rounded-l-md dark:bg-gray-600 dark:text-gray-400 dark:border-gray-600"
			>
				<svg
					class="w-6 h-6 text-gray-800 dark:text-white"
					aria-hidden="true"
					xmlns="http://www.w3.org/2000/svg"
					fill="none"
					viewBox="0 0 22 20"
				>
					<path
						stroke="currentColor"
						stroke-linecap="round"
						stroke-linejoin="round"
						stroke-width="2"
						d="M20 10a28.076 28.076 0 0 1-1.091 9M6.231 2.37a8.994 8.994 0 0 1 12.88 3.73M1.958 13S2 12.577 2 10a8.949 8.949 0 0 1 1.735-5.307m12.84 3.088c.281.706.426 1.46.425 2.22a30 30 0 0 1-.464 6.231M5 10a6 6 0 0 1 9.352-4.974M3 19a5.964 5.964 0 0 1 1.01-3.328 5.15 5.15 0 0 0 .786-1.926m8.66 2.486a13.96 13.96 0 0 1-.962 2.683M6.5 17.336C8 15.092 8 12.846 8 10a3 3 0 1 1 6 0c0 .75 0 1.521-.031 2.311M11 10.001c0 3 0 6-2 9"
					/>
				</svg>
			</span>			

			<!-- 60% width -->
			<input
				bind:value={inputCode}
				type="text"
				placeholder="Generated Code Here (/gencode)"
				class="w-80 rounded-none rounded-r-lg bg-gray-50 border border-gray-300 text-gray-900 focus:ring-blue-500 focus:border-blue-500 block flex-1 min-w-0 text-sm p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
			/>
		</div>

		<!-- button which when pressed, submits it -->
		<button
			class="bg-blue-500 hover:bg-blue-400 text-white font-bold rounded py-2 px-8 mt-8"
			on:click={async () => {
				const signer = await get_wallet_for_chain(CHAIN_ID);
				userAddress = (await signer.getAccounts())[0].address;

				doConnection(userAddress, inputCode);
			}}>Link wallet with Minecraft Code</button
		>
	</div>
</main>
