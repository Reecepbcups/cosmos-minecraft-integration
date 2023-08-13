<script lang="ts">
	const url = `https://raw.githubusercontent.com/Reecepbcups/cosmossdk-minecraft-pvp-server/main/GUIDE.md`;
	const fancyURL = `https://github.com/Reecepbcups/cosmossdk-minecraft-pvp-server/blob/main/GUIDE.md`;

	async function getGithubGuide(): Promise<string> {
		// fetch the data from the url and return it
		const response = await fetch(url);
		if (response.ok) {
			const text = await response.text();
			return text;
		}

		return 'Error loading guide from Github...';
	}
</script>

<main>
	<div class="flex flex-col justify-center items-center mt-32">
		<h1 class="text-4xl font-bold text-gray-900 dark:text-white">Guide</h1>
		<hr class="w-1/2 mt-2 mb-2 border-gray-300 dark:border-gray-600" />

		<!-- {#await getGithubGuide()}
			<p>loading...</p>
		{:then guide}						
			<div class="text-white"
			>{@html marked(guide)}</div>			
		{:catch error}
			<p>{error}</p>
		{/await} -->

		<a
			href={fancyURL}
			target="_blank"
			rel="noopener noreferrer"
			class="px-4 py-2 mt-4 text-white bg-blue-500 rounded-md hover:bg-blue-600"
			>View Guide</a
		>
	</div>
</main>
