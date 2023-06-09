// Express
import { Request, Response } from 'express';
import { makePayment, getServersEscrowAccountInfo } from '../services/dao.service';

export const makePaymentToPlayer = async (req: Request, res: Response) => {
    const {secret, wallet, ucraft_amount, description} = req.body;

    // console.log("makePayment:", secret, wallet, ucraft_amount, description);

    const response = await makePayment(secret, wallet, ucraft_amount, description);
    if (response) return res.status(200).json(response);
    else return res.status(404).json({ message: 'No Real Estate NFTs found for this wallet' });
};


export const getServerEscrowWallet = async (req: Request, res: Response) => {
    const response = await getServersEscrowAccountInfo();
    if (response) return res.status(200).json(response);
    else return res.status(404).json({ message: 'ERROR: No escrow account found in config...' });
};

export default {        
    getServerEscrowWallet,
    makePaymentToPlayer
};