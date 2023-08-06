import { toast } from '@zerodevx/svelte-toast'
import type { SvelteToastOptions } from '@zerodevx/svelte-toast/stores';	

export const success_notification = (m: string | SvelteToastOptions, duration?: number) => toast.push(m, {
    theme: {
        '--toastBackground': '#4BB543',
        '--toastColor': 'black',
        '--toastBarBackground': 'white',        
    },
    duration: duration || 5000
})	

export const error_notification = (m: string | SvelteToastOptions, duration?: number) => toast.push(m, {
    theme: {
        '--toastBackground': 'red',
        '--toastColor': 'white',
        '--toastBarBackground': 'white'
    },
    duration: duration || 5000
})