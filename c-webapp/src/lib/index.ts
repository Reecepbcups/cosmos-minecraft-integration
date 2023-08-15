// place files you want to import through the `$lib` alias in this folder.

const isTestnet = false;

export const CHAIN_ID = isTestnet ? "uni-6" : "juno-1";
export const DENOM = isTestnet ? "ujunox" : "ujuno";
export const FEE_AMT = isTestnet ? "500" : "18750";
export const GAS_AMT = isTestnet ? '250000' : '250000';
export const EXPLORER_TX = isTestnet ? "https://testnet.mintscan.io/juno-testnet/txs" : "https://mintscan.io/juno/transactions";
export const rpcEndpoint = isTestnet ? "https://uni-rpc.reece.sh/" : "https://rpc.juno.strange.love/";
export const ENDPOINT = 'https://craft-api.reece.sh';
export const FAUCET = isTestnet ? 'https://faucet.reece.sh' : 'Faucet Is Disabled';


