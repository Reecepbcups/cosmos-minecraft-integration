import type { Window as KeplrWindow } from '@keplr-wallet/types';		
import type { OfflineAminoSigner } from '@cosmjs/amino';
import type { OfflineDirectSigner } from '@cosmjs/proto-signing';

export const get_wallet_for_chain = async (
    chain_id: string
): Promise<OfflineAminoSigner | OfflineDirectSigner> => {
    let keplr: KeplrWindow  = window as KeplrWindow;
    if (keplr === undefined) {			
        // error_notification('Keplr not found');
        throw new Error('Keplr not found');
    }

    let signer = keplr.getOfflineSignerAuto;
    if (signer === undefined) {
        throw new Error('Keplr not found');
    }

    let s = signer(chain_id);
    return s;
};