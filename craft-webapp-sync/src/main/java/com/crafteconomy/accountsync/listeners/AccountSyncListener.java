package com.crafteconomy.accountsync.listeners;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.crafteconomy.accountsync.dao.SyncCodeDAO;
import com.crafteconomy.accountsync.dao.ConnectionsDAO;
import com.crafteconomy.accountsync.data.Connections;
import com.crafteconomy.accountsync.data.UserSyncCode;
import com.crafteconomy.blockchain.utils.Util;

public class AccountSyncListener implements Listener {    

    /**
     * On Join, check if their account is linked to the MongoDB collection (minecraftr, keplr, and discordID)
     * If not, prompt them to link their account to the webapp.
     *
     * @param event the player join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Check if uuid is in the mongo collection "connections" (if they are synced, they are good to play)
        Optional<Connections> connected = ConnectionsDAO.INSTANCE.get(uuid);
        // Their secret code linked to uuid
        Optional<UserSyncCode> syncCodeData = SyncCodeDAO.INSTANCE.get(uuid);

        // User is not synced up with the webapp
        if (connected.isEmpty()) {
            System.out.println("Player " + player.getName() + " is not synced in 'connections'");
            player.sendMessage(Util.color("&c&l&nYou are not synced with the webapp! Please register and sync your account."));
            UserSyncCode.generateCode(player);
            return;
        }

        // User has synced with the webapp as we got a document back
        Connections connections = connected.get();

        // Likely a redundant check which will never be used
        if (connections.getKeplrId() == null || connections.getKeplrId().length() == 0) {
            System.out.println("Somehow " + uuid + " did not have a keplrId in their connected, even tho the document exist. Webapp issue");
            player.sendMessage("Your sync status was not complete, Please resync to the webapp!");  
            Connections.unlink(uuid);
            UserSyncCode.generateCode(player);
        }            

        // If they still have a secret code for their account, delete it now. They are already synced
        syncCodeData.ifPresent(SyncCodeDAO.INSTANCE::delete);
    }
}