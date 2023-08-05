package com.crafteconomy.accountsync.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.Document;

import com.crafteconomy.accountsync.data.UserSyncCode;
import com.crafteconomy.blockchain.CraftBlockchainPlugin;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

public enum SyncCodeDAO {
    INSTANCE;

    MongoDatabase db = null;

    private SyncCodeDAO() {
        db = CraftBlockchainPlugin.getInstance().getMongo().getDatabase();
    }

    public String collectionName() {
        return "webappSyncCodes";
    }

    public MongoCollection<Document> getCollection() {
        return db.getCollection(collectionName());
    }

    public void delete(UserSyncCode userdata) {
        getCollection().deleteOne(Filters.eq("_id", userdata.getUuid().toString()));
    }

    public Optional<UserSyncCode> get(UUID uuid) {
        Document found = getCollection().find(Filters.eq("_id", uuid.toString())).first();
        if (found == null) return Optional.empty();
        
        UserSyncCode userdata = new UserSyncCode();
        userdata.load(found);
        return Optional.of(userdata);
    }


    public List<UserSyncCode> getAll() {
        return null; // never used
    }


    public void save(UserSyncCode userdata) {
        getCollection().replaceOne(Filters.eq("_id", userdata.getUuid().toString()), userdata.toMongoDocument(), new ReplaceOptions().upsert(true));        
    }
}
