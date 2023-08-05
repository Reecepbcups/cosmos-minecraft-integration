import type { Window as KeplrWindow } from '@keplr-wallet/types';

// ledger support
import type { OfflineAminoSigner } from '@cosmjs/amino';
import type { OfflineDirectSigner } from '@cosmjs/proto-signing';



export const get_wallet_for_chain = async (chain_id: string): Promise<OfflineAminoSigner | OfflineDirectSigner> => {
    // open keplr
    const keplr = window as KeplrWindow;
    if (keplr === undefined) {			
        throw new Error('Keplr not found');
    }		

    const signer = keplr.getOfflineSignerAuto;
    if (signer === undefined) {
        throw new Error('Keplr not found');
    }

    return signer(chain_id);
};