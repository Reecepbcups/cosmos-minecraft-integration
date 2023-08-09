<script lang="ts">	
	
	async function getGithubGuide(): Promise<string> {
		const url = `https://raw.githubusercontent.com/Reecepbcups/cosmossdk-minecraft-pvp-server/main/GUIDE.md`;		

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
					return;
				}
				
				return data;
			})
			.catch((err) => {
				console.log(err);				
				// error_notification(`Error: ${err.error}`);
				return err.error;
			});

		return "";

	}
</script>

<main>
	<div class="flex flex-col justify-center items-center mt-32">		
		<h1 class="text-4xl font-bold text-gray-900 dark:text-white">Guide</h1>
		<hr class="w-1/2 mt-2 mb-2 border-gray-300 dark:border-gray-600" />		

		<!-- on load, run getGithubGuide -->
		{#await getGithubGuide()}
			<p>loading...</p>
		{:then guide}
			<p>{guide}</p>
		{:catch error}
			<p>{error}</p>
		{/await}
	</div>
</main>
