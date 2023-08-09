<script lang="ts">
    import Navigation from '../components/Navigation.svelte'
    import Transactions from '../components/Transactions.svelte'
    import MCAuth from '../components/MinecraftAuth.svelte'    
	import Faucet from '../components/Faucet.svelte';
	import Guide from '../components/Guide.svelte';
    
    let currentPath = "/";
</script>

<div>
    <Navigation activeTab={"/"} on:pathChange={(event) => {
        console.log("path changed" + event.detail.text)
        currentPath = event.detail.text
    }}/>

    
    
    {#if currentPath === "/"}
        <Transactions />
    {:else if currentPath === "/auth"}
        <MCAuth />
    {:else if currentPath === "/faucet"}
        <Faucet />
    {:else if currentPath === "/guide"}
        <Guide />
    {/if}    
</div>

