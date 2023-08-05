<!--  Reece Williams | August 2023 | Craft Webapp -->
<script lang="ts">
	import { SvelteEasyToast } from 'svelte-easy-toast';

	import Transactions from '../components/Transactions.svelte';

	import { page } from '$app/stores';		

	const fee = {amount: [{	amount: "100000",	denom: "ujuno",},], gas: "200000",};
		
	const rpcEndpoint = "https://uni-rpc.reece.sh/"					

	let allowed_pages = new Map([
		['main', 'Main Page'],
	]);

	export let selectedTab = $page.url.searchParams.get('page')?.toLowerCase() || 'main';
	if (selectedTab === null) { 
		selectedTab = 'feeshare';
	} else if(!allowed_pages.has(selectedTab)) {
		selectedTab = 'NOT_FOUND';
	}

	const setPageUrl = (page: string) => {
		const url = new URL(window.location.href);
		page = page.toLowerCase();

		if(!allowed_pages.has(page)) {
			selectedTab = 'NOT_FOUND';
		}

		url.searchParams.set('page', page);
		window.history.pushState({}, '', url);
	};


</script>

<SvelteEasyToast />

<div>
	<!-- Future: We can allow for multiple tabs of actions -->
	<!-- <div class="nav-bar">
		{#each [...allowed_pages] as [page, name]}
			<button class="nav-button {selectedTab === page ? 'selected' : ''}" on:click={() => {
				selectedTab = page

				setPageUrl(page);
			}}>{name}</button>
		{/each}		
	</div> -->


	<div class="page">

		<div class="page-container">
			{#if selectedTab === 'main'}
				<Transactions />
			<!-- {#if selectedTab === 'feeshare'}
				<FeeShare rpcEndpoint={rpcEndpoint} fee={fee} />
			{:else if selectedTab === 'tokenfactory'}
				<TokenFactory rpcEndpoint={rpcEndpoint} fee={fee} />
			{:else if selectedTab === 'migration'}	
				<Migration />
			{:else if selectedTab === 'usermigrate'}	
				<UserMigration />			 -->
			{:else if selectedTab === 'NOT_FOUND'}
				<br>
				<center>
					<div class="page-container">
						<h1>Page Not Found</h1>
						<p>Sorry, the page you are looking for does not exist.</p>
					</div>
				</center>
			{/if}
		</div>
	</div>
</div>


<style>
	.page {
		padding-top: 30px;
		width: 100%;
		margin: auto;
		display: grid;
		position: relative;
	}

	/* .nav-bar {
		padding-top: 50px;
		left: 0px;
		grid-template-columns: 20% 20% 20% 20% 20%;
		display: grid;
		width: 100%;
	} */

	.page-container {
		width: 100%;
		margin: auto;
	}

	.nav-button {
		border: none;
		border-radius: 8px;
		background-color: #555;
		color: #f5f5f5;
		cursor: pointer;
		font-size: 0.7rem;
		width: 80%;
		/* margin: auto; */
		margin-top: 10px;
	}

	@media (min-width:780px){
		.nav-button {
			border: none;
			border-radius: 8px;
			background-color: #555;
			color: #f5f5f5;
			cursor: pointer;
			font-size: 1.1rem;
			width: 80%;
			margin: auto;
		}
	}

	.nav-button.selected {
		background-color: #f5f5f5;
		color: #222;
	}
</style>
