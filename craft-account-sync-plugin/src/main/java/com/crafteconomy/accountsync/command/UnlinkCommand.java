package com.crafteconomy.accountsync.command;

import com.crafteconomy.accountsync.data.Connections;
import com.crafteconomy.blockchain.utils.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.UUID;

/**
 * Command to unlink a player's connections so they can relink
 */
public class UnlinkCommand implements Listener, CommandExecutor {

    /**
     * Player's confirming to unlink
     */
    private final HashSet<UUID> unlinking = new HashSet<>();

    @EventHandler
    public void onJoin(PlayerQuitEvent event) {
        unlinking.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {          
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (unlinking.contains(uuid)) {
            Connections.unlink(uuid);
            Util.colorMsg(sender, "&c&lUnlinked account! You must now relink your accounts before you can do anything.");
            unlinking.remove(uuid);
            return true;
        }

        String msg = "&c&lAre you sure you want to unlink your account?\n\n&cYou will have to relink your keplr wallet before you can play again.\n&cRun &n/unlink&c again to confirm!";
        Util.colorMsg(sender, msg);
        unlinking.add(uuid);
        return true;
    }

}
