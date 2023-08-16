package com.crafteconomy.blockchain.transactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.crafteconomy.blockchain.CraftBlockchainPlugin;
import com.crafteconomy.blockchain.storage.RedisManager;

import redis.clients.jedis.Jedis;

public class PendingTransactions {
    
    // Randomly Generated TxUUID, Tx
    private static Map<UUID, Tx> pending = new HashMap<>();

    private static PendingTransactions instance = null;

    private PendingTransactions() {  
        // singleton      
    }

    public void addPending(UUID TxID, Tx tx) {
        // ID, TxInfo
        pending.put(TxID, tx);
    }

    public void removePending(UUID TxID) {
        pending.remove(TxID);
    }

    public Tx getTxFromID(UUID txID) {
        return pending.get(txID);
    }

    public Set<UUID> getKeys() {
        return pending.keySet();
    }

    public boolean expireTransaction(UUID txID) {
        if(getKeys().contains(txID)) {            
            try (Jedis jedis = RedisManager.getInstance().getRedisConnection()) {
            
                // deletes redis keys which are in pending keys since we do not save to DB
                String key = "tx_*_"+ txID.toString();
                // String value = jedis.get(key);

                jedis.keys(key).forEach(k -> {
                    // jedis.unlink(k); // deletes the key
                    jedis.setex(k, 1, "expired");
                    CraftBlockchainPlugin.log("[PendingTxs.java] Expired " + key + " from redis");
                });  

                // removePending(txID);
                return true;

            } catch (Exception e) {
                CraftBlockchainPlugin.log("[PendingTxs.java] Failed to clear transactions. Make sure pool is open");
            }                        
        }
        return false;
    }

    public static void clearUncompletedTransactionsFromRedis() {
        try (Jedis jedis = RedisManager.getInstance().getRedisConnection()) {
            
            // deletes redis keys which are in pending keys since we do not save to DB
            for(UUID TxID : pending.keySet()) {

                String key = "tx_*_"+ TxID.toString();
                // String value = jedis.get(key);

                jedis.keys(key).forEach(k -> {
                    jedis.unlink(k); // deletes the key
                    pending.remove(TxID);
                    CraftBlockchainPlugin.log("[PendingTxs.java] Removed " + key + " from redis");
                });  
            }

        } catch (Exception e) {
            CraftBlockchainPlugin.log("[PendingTxs.java] Failed to clear transactions. Make sure pool is open");
        }
    }

    public static List<UUID> getRedisTransactionsFromWallet(String wallet) {
        List<UUID> txs = new ArrayList<>();
        if(wallet == null) return txs;

        try (Jedis jedis = RedisManager.getInstance().getRedisConnection()) {            
            String key = "tx_"+wallet+"_*";

            jedis.keys(key).forEach(k -> {                
                try {
                    System.out.println("Found " + key + " -> " + k);
                    String[] split = k.split("_");
                    String TxID = split[2];
                    txs.add(UUID.fromString(TxID));
                } catch (Exception e) {
                    CraftBlockchainPlugin.log("[getTransactionsFromWallet] Failed to get txID from key " + k);
                }
            });            
        } catch (Exception e) {
            CraftBlockchainPlugin.log("[getTransactionsFromWallet] " + e.getMessage());            
        }

        return txs;
    }

    // expires them and deletes from redis / pending
    public static void clearPendingTransactionsFromWallet(String wallet) {
        for(UUID TxID : pending.keySet()) {            
            Tx tx = pending.get(TxID);
            if(tx.getFromWallet().equalsIgnoreCase(wallet)) {                     
                PendingTransactions.getInstance().expireTransaction(TxID);                
            }
        }
    }

    // deprecated
    public static void clearTransactionsFromWallet(String wallet) {
        if(wallet == null) return;
        

        // List<UUID> txs = getRedisTransactionsFromWallet(wallet);

        try (Jedis jedis = RedisManager.getInstance().getRedisConnection()) {
            
            // deletes redis keys which are in pending keys since we do not save to DB
            for(UUID TxID : pending.keySet()) {

                // String key = "tx_"+wallet+"_"+ TxID.toString();
                String key = "tx_"+wallet+"_*";
                // String value = jedis.get(key);                

                jedis.keys(key).forEach(k -> {
                    System.out.println("Deleting " + key + " -> " + k);
                    jedis.unlink(k); // deletes the key

                    if(pending.containsKey(TxID)) {
                        pending.remove(TxID);
                    }                    
                    CraftBlockchainPlugin.log("[PendingTxs.java] Removed " + key + " from redis");
                });                  
            }            
        } catch (Exception e) {
            CraftBlockchainPlugin.log("[clearTransactionsFromWallet.java] " + e.getMessage());            
        }
    }

    public static PendingTransactions getInstance() {       
        if(instance == null) {
            instance = new PendingTransactions();            
        }
        
        return instance;
    }

}
