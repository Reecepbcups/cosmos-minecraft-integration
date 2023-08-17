package com.crafteconomy.accountsync.command;

import com.crafteconomy.accountsync.data.UserSyncCode;
import com.crafteconomy.blockchain.utils.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GenerateCodeCommand implements CommandExecutor {    

    public GenerateCodeCommand() {
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {    
        if(sender instanceof ConsoleCommandSender) {
            Util.colorMsg(sender, "Only players can use this command");
            return true;
        }        

        UserSyncCode.generateCode((Player) sender);
        return true;
    }
}
