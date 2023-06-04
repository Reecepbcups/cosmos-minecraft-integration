package com.crafteconomy.blockchain.commands.wallet.subcommands;

import com.crafteconomy.blockchain.CraftBlockchainPlugin;
import com.crafteconomy.blockchain.commands.SubCommand;
import com.crafteconomy.blockchain.core.request.BlockchainRequest;
import com.crafteconomy.blockchain.utils.Util;

import org.bukkit.command.CommandSender;

public class WalletSupply implements SubCommand {

    CraftBlockchainPlugin plugin = CraftBlockchainPlugin.getInstance();

    @Override
    public void onCommand(CommandSender sender, String[] args) { 
                

        BlockchainRequest.getTotalSupply(plugin.getTokenDenom()).thenAccept((supply) -> {
            Util.colorMsg(sender, "Total "+plugin.getTokenDenom()+" supply is " + Util.formatNumber(supply));        
        });
        // BlockchainRequest.getTotalSupply("uexp").thenAccept((supply) -> {
        //     Util.colorMsg(sender, "Total uexp supply is " + Util.formatNumber(supply));        
        // });
    }
}
