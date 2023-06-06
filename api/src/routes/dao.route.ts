const express = require('express');
const router = express.Router();
import daoControler from '../controllers/dao.controler';

// Routes for standalone nft contracts (middleware)
router.get('/escrow_account_info', daoControler.getServerEscrowWallet)

router.post('/make_payment', daoControler.makePaymentToPlayer)
export default router;