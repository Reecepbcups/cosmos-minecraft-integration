<script lang="ts" context="module">
    // import { toast, SvelteToast } from '@zerodevx/svelte-toast'			
	// import type {SvelteToastOptions} from '@zerodevx/svelte-toast/stores'

	// https://github.com/johndavedecano/svelte-easy-toast

	// import toast, { Toaster, type ToastOptions } from 'svelte-french-toast';    	
	// const toast_style: ToastOptions = {
	// 	position: 'top-right',
	// 	duration: 6000,
	// 	style: 'background: #333; color: #fff; width: 20%; font-size: 1.0rem; border-radius: 10px;',		
	// };

	import { SvelteEasyToast, toast } from 'svelte-easy-toast';

	// types 
	// dark, danger, success, info, warning, default, error

	enum ToastType {
		primary = 'primary',
		dark = 'dark',
		danger = 'danger',
		success = 'success',
		info = 'info',
		warning = 'warning',
		default = 'default',
		error = 'error',
	}
     
     const showToast = (title: string, message: string, tt: ToastType) => {
	    toast({
			type: tt.toString(), // dark, danger, success, info, warning, default, error
			position: 'top-right', // top-left, top-center, bottom-left, bottom-right, bottom-center
			title: title,
			text: message,
			closeOnClick: false,
			delay: 6000,
			showClose: true,			
	    });
     }

    export function success_notification(message: string) {
        // toast.success(message, toast_style);
		showToast("Success", message, ToastType.success);
    }

    export function error_notification(message: string) {
		// toast.error(message, toast_style);
		showToast("Error", message, ToastType.error);
    }    

</script>

<style>
	
</style>