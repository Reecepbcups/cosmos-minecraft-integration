<script lang="ts">
	import type { Window as KeplrWindow } from '@keplr-wallet/types';		
	import type { OfflineAminoSigner } from '@cosmjs/amino';
	import type { OfflineDirectSigner, EncodeObject } from '@cosmjs/proto-signing';		

	import { success_notification, error_notification } from './Status.svelte';

	// == Configuration ==	
	export let ENDPOINT: string;
	export let CHAIN_ID: string;	

	// Application Logic
	let userAddress: string;

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

	async function doConnection(keplrWallet: string, minecraftCode: string) {
		console.log(keplrWallet, minecraftCode);

		if(minecraftCode.length == 0) {
			error_notification("Minecraft Code cannot be empty");
			return;
		}

		if(keplrWallet.length == 0) {
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

				let v = "test"
				v.includes("test")

				// convert data to a string
				let dataString = JSON.stringify(data);

				if(dataString.includes("not found")) {
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

	let inputCode = "";
</script>

<main>
	<h1>Minecraft Wallet Authentication</h1>
	
	<input
		type="text"
		id="minecraftCode"
		name="minecraftCode"
		minlength="30"
		maxlength="50"
		size="35"
		placeholder="Enter your Minecraft Code (/gencode)"
		required
		bind:value={inputCode}
	/>
	
	<button
		style="margin-left:10px"
		on:click={async () => {
			const signer = await get_wallet_for_chain(CHAIN_ID);
			userAddress = (await signer.getAccounts())[0].address;

			doConnection(userAddress, inputCode);
		}}>Link wallet with Minecraft Code</button>
</main>

<style>
	button {
		border-radius: 10px;
		font-weight: bold;
		padding: 5px;
		border: 1px solid black;
	}

	/* make the input box larger, and rounded */
	input[type=text] {
		width: 50%;
		padding: 12px 20px;
		margin: 8px 0;
		box-sizing: border-box;
		border-radius: 4px;
		border: 1px solid #ccc;
		outline: none;
	}
</style>
