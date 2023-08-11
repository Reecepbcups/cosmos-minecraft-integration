import type { Window as KeplrWindow } from '@keplr-wallet/types';		
import type { OfflineAminoSigner } from '@cosmjs/amino';
import type { OfflineDirectSigner } from '@cosmjs/proto-signing';

import type { ChainInfo, FeeCurrency } from '@keplr-wallet/types';

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
    
    let chainInfo = recommend_chain(chain_id);
    await keplr.keplr?.experimentalSuggestChain(chainInfo);
    
    let s = signer(chain_id);
    return s;
};

function recommend_chain (chain_id: string): ChainInfo {    
    switch (chain_id) {
        case "uni-6":
            let uniwalletPrefix = "uni";
            let unirpc = "https://uni-rpc.reece.sh";
            let uniapi = "https://uni-api.reece.sh";
            let unitoken: FeeCurrency = {
                    coinDenom: "JUNOX",
                    coinMinimalDenom: "ujunox",
                    coinDecimals: 6,                        
                    coinGeckoId: "juno-network",        
                    gasPriceStep: {
                        low: 0.0025,
                        average: 0.003,
                        high: 0.004
                    },
                }
            return {
                rpc: unirpc,
                bech32Config: {
                    bech32PrefixAccAddr:  uniwalletPrefix,
                    bech32PrefixAccPub:   uniwalletPrefix + "pub",
                    bech32PrefixValAddr:  uniwalletPrefix + "valoper",
                    bech32PrefixValPub:   uniwalletPrefix + "valoperpub",
                    bech32PrefixConsAddr: uniwalletPrefix + "valcons",
                    bech32PrefixConsPub:  uniwalletPrefix + "valconspub"
                },
                currencies: [unitoken],
                feeCurrencies: [unitoken],
                bip44: {
                    coinType: 118,
                },
                chainId: chain_id,
                chainName: "Juno Testnet",
                rest: uniapi,
                stakeCurrency: {
                    coinDenom: unitoken.coinDenom,
                    coinMinimalDenom: unitoken.coinMinimalDenom,
                    coinDecimals: unitoken.coinDecimals,
                    coinGeckoId: unitoken.coinGeckoId,                    
                },
                features: ["stargate", "ibc-transfer", "cosmwasm"],                
            };
        case "juno-1":
            let JunowalletPrefix = "juno";
            let Junorpc = "https://rpc.juno.strange.love";
            let Junoapi = "https://api.juno.strange.love";
            let Junoname = "Juno Network";
            let Junotoken: FeeCurrency = {
                    coinDenom: "JUNO",
                    coinMinimalDenom: "ujuno",
                    coinDecimals: 6,                        
                    coinGeckoId: "juno-network",        
                    gasPriceStep: {
                        low: 0.075,
                        average: 0.08,
                        high: 0.09
                    },
                }
            return {
                rpc: Junorpc,
                bech32Config: {
                    bech32PrefixAccAddr:  JunowalletPrefix,
                    bech32PrefixAccPub:   JunowalletPrefix + "pub",
                    bech32PrefixValAddr:  JunowalletPrefix + "valoper",
                    bech32PrefixValPub:   JunowalletPrefix + "valoperpub",
                    bech32PrefixConsAddr: JunowalletPrefix + "valcons",
                    bech32PrefixConsPub:  JunowalletPrefix + "valconspub"
                },
                currencies: [Junotoken],
                feeCurrencies: [Junotoken],
                bip44: {
                    coinType: 118,
                },
                chainId: chain_id,
                chainName: Junoname,
                rest: Junoapi,
                stakeCurrency: {
                    coinDenom: Junotoken.coinDenom,
                    coinMinimalDenom: Junotoken.coinMinimalDenom,
                    coinDecimals: Junotoken.coinDecimals,
                    coinGeckoId: Junotoken.coinGeckoId,                    
                },
                features: ["stargate", "ibc-transfer", "cosmwasm"],                
            };
        // case "chimba-testnet":
        //     let testwalletPrefix = "juno";
        //     let testrpc = "https://rpc.testnet.chimbablockchain.io/";
        //     let testapi = "https://testnet.chimbablockchain.io/";
        //     let testname = "chimba-testnet";
        //     let testtoken: FeeCurrency = {
        //             coinDenom: "JUNO",
        //             coinMinimalDenom: "ujuno",
        //             coinDecimals: 6,                        
        //             coinGeckoId: "juno-network",        
        //             gasPriceStep: {
        //                 low: 0.075,
        //                 average: 0.08,
        //                 high: 0.09
        //             },
        //         }
        //     return {
        //         rpc: testrpc,
        //         bech32Config: {
        //             bech32PrefixAccAddr:  testwalletPrefix,
        //             bech32PrefixAccPub:   testwalletPrefix + "pub",
        //             bech32PrefixValAddr:  testwalletPrefix + "valoper",
        //             bech32PrefixValPub:   testwalletPrefix + "valoperpub",
        //             bech32PrefixConsAddr: testwalletPrefix + "valcons",
        //             bech32PrefixConsPub:  testwalletPrefix + "valconspub"
        //         },
        //         currencies: [testtoken],
        //         feeCurrencies: [testtoken],
        //         bip44: {
        //             coinType: 118,
        //         },
        //         chainId: chain_id,
        //         chainName: testname,
        //         rest: testapi,
        //         stakeCurrency: {
        //             coinDenom: testtoken.coinDenom,
        //             coinMinimalDenom: testtoken.coinMinimalDenom,
        //             coinDecimals: testtoken.coinDecimals,
        //             coinGeckoId: testtoken.coinGeckoId,                    
        //         },
        //         features: ["stargate", "ibc-transfer", "cosmwasm"],                
        //     };
    
        default:
            break;
    }
    
    throw new Error('Chain not found');
}
