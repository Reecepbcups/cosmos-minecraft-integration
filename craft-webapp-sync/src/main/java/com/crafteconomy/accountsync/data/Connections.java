package com.crafteconomy.accountsync.data;

import java.util.UUID;

import com.crafteconomy.accountsync.dao.ConnectionsDAO;
import org.bson.Document;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a player's account connections
 */
public class Connections {

    /**
     * Minecraft UUID
     */
    @Getter
    @Setter
    private UUID uuid;

    /**
     * Keplr wallet id
     */
    @Getter
    private String keplrId;

    /**
     * Discord id
     */
    @Getter
    private String discordId;

    public void load(Document document) {
        uuid = UUID.fromString(document.getString("minecraftId"));
        keplrId = document.getString("keplrId");
        discordId = document.getString("discordId") != null ? document.getString("discordId") : null;
    }

    public Document toMongoDocument() {
        Document document = new Document("minecraftId", uuid.toString());
        document.put("keplrId", keplrId);
        document.put("discordId", discordId);
        
        return document;
    }

    /**
     * Deletes the connection for a player (unlink)
     *
     * @param uuid Player who's connection to delete
     */
    public static void unlink(UUID uuid) {
        Connections connection = new Connections();
        connection.setUuid(uuid);

        ConnectionsDAO.INSTANCE.delete(connection);
    }
}
