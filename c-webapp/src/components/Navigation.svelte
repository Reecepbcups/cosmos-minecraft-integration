

<script lang="ts">    
    import { Navbar, NavBrand, NavLi, NavUl, NavHamburger } from 'flowbite-svelte'

    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();

    const routes = new Map([
        ['/', 'Transactions'],
        ['/auth', 'Authentication'],
        ['/faucet', 'Faucet']
    ])
     
    export let activeTab: string;

    function changePath(path: string) {
        dispatch('pathChange', {text: path});
    }
</script>

<body>
    <Navbar let:hidden let:toggle rounded color="form" class="md:col-span-1 md:mr-32 md:mt-4">
        <NavBrand href="/">            
            <span class="self-center whitespace-nowrap text-xl font-semibold dark:text-white">Juno Craft</span>
        </NavBrand>
        <NavHamburger on:click={toggle} />
        <NavUl {hidden}>   
            <!-- on tab click, fire an event which updates to the new endpoint -->
            {#each routes as data}
                <NavLi on:click={() => changePath(data[0])} href={data[0]} active={activeTab === data[0]}>{data[1]}</NavLi>
            {/each}
        </NavUl>
    </Navbar>
</body>
