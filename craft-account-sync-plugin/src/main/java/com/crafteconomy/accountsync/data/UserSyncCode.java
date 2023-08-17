package com.crafteconomy.accountsync.data;

import java.util.UUID;

import com.crafteconomy.accountsync.dao.SyncCodeDAO;
import com.crafteconomy.blockchain.CraftBlockchainPlugin;
import com.crafteconomy.blockchain.api.IntegrationAPI;
import com.crafteconomy.blockchain.utils.Util;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.Document;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * Stores a secret that associates with a player uuid. Used for linking
 */
public class UserSyncCode {

    private static IntegrationAPI api = CraftBlockchainPlugin.getAPI();

    /**
     * Minecraft uuid
     */
    @Getter
    @Setter
    private UUID uuid;

    /**
     * Generated secret code
     */
    @Getter
    @Setter
    private String code;

    public void load(Document document) {
        uuid = UUID.fromString(document.getString("_id"));
        code = document.getString("code");
    }

    public Document toMongoDocument() {
        Document document = new Document("_id", uuid.toString());
        document.put("code", code);
        return document;
    }

    /**
     * Generate this sync code for a player
     *
     * @param player Player's who's uuid to link to
     */
    public static void generateCode(Player player) {
        // Should we change to random ascii in the future?
        String newCode = RandomStringUtils.randomAlphanumeric(30).toUpperCase();

        // set this new code as the users in the database (so the API can query it)
        UserSyncCode userdata = new UserSyncCode();
        userdata.setUuid(player.getUniqueId());
        userdata.setCode(newCode);
        SyncCodeDAO.INSTANCE.save(userdata);

        // website/auth?code=XXXXX
        String URL = api.getWebAppAddress() + "auth?code=" + newCode;

        // Util.clickableCopy(player, newCode, "\n&aGenerated your sync code! &f&o&n(( Click to copy ))", "Click this text to copy it and paste to the webapp!");
        Util.clickableWebsite(player, URL, "\n&a&nClick here&a to visit our website to sync your account!\n", "Click to open");
    }
}
