package com.crafteconomy.accountsync.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.Document;

import com.crafteconomy.accountsync.data.Connections;
import com.crafteconomy.blockchain.CraftBlockchainPlugin;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * Get a player's connection based on their minecraft UUID
 */
public enum ConnectionsDAO {
    INSTANCE;

    MongoDatabase db = null;

    private ConnectionsDAO() {
        db = CraftBlockchainPlugin.getInstance().getMongo().getDatabase();
    }
    
    public String collectionName() {
        return "connections";
    }

    public MongoCollection<Document> getCollection() {
        return db.getCollection(collectionName());
    }
    
    public void delete(Connections connection) {
        getCollection().deleteOne(Filters.eq("minecraftId", connection.getUuid()));
    }

    public Optional<Connections> get(UUID uuid) {
        Document found = getCollection().find(Filters.eq("minecraftId", uuid.toString())).first();
        if (found == null) return Optional.empty();
        
        Connections c = new Connections();
        c.load(found);

        return Optional.of(c);
    }

    public List<Connections> getAll() {
        List<Connections> allConnections = new ArrayList<>();

        // Load all in collection
        getCollection().find().into(new ArrayList<>()).forEach(doc -> {
            Connections connection = new Connections();
            connection.load(doc);
            allConnections.add(connection);
        });

        return allConnections;
    }

    /**
     * Saving connections is handled by webapp
     */    
    public void save(Connections arg0) {
    }
}
