package com.crafteconomy.accountsync;

import org.bukkit.plugin.java.JavaPlugin;

import com.crafteconomy.accountsync.command.GenerateCodeCommand;
// import com.crafteconomy.accountsync.command.UnlinkCommand;
import com.crafteconomy.accountsync.listeners.AccountSyncListener;

public class CraftAccountSyncPlugin extends JavaPlugin {

    private CraftAccountSyncPlugin instance;
    public CraftAccountSyncPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        AccountSyncListener asl = new AccountSyncListener();
        // UnlinkCommand unlinkCommand = new UnlinkCommand();
        
        getCommand("gencode").setExecutor(new GenerateCodeCommand());
        // getCommand("unlink").setExecutor(unlinkCommand);

        getServer().getPluginManager().registerEvents(asl, this);
        // getServer().getPluginManager().registerEvents(unlinkCommand, this);
    }

    @Override
    public void onDisable() { }
}
