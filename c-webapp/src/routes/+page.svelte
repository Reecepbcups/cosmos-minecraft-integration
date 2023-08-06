<script lang="ts">
    import Navigation from '../components/Navigation.svelte'
    import Transactions from '../components/Transactions.svelte'
    import MCAuth from '../components/MinecraftAuth.svelte'

    import { SvelteToast } from '@zerodevx/svelte-toast'    

    import { rpcEndpoint, ENDPOINT, CHAIN_ID } from '../lib/index'

    import { Footer, FooterCopyright, FooterLinkGroup, FooterLink } from 'flowbite-svelte'

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
    {/if}    
</div>

