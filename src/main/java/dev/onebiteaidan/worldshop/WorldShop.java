package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.Controller.Commands.WorldshopCommand;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners.*;
import dev.onebiteaidan.worldshop.Controller.PlayerManager;
import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.Model.DataManagement.Config;
import dev.onebiteaidan.worldshop.Utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class WorldShop extends JavaPlugin {

    private static Config config;
    private static StoreManager storeManager;
    private static PlayerManager playerManager;


    @Override
    public void onEnable() {
        // Setup the Logger
        Logger.setPlugin(this);

        // Checks if data folder exists
        if (!getDataFolder().exists()) {
            this.getLogger().info("Datafolder for WorldShop Does not Exist. Creating now...");
            if (getDataFolder().mkdirs()) {
                Logger.info("Successfully created datafolder.");
            } else {
                Logger.severe("Failed to create the datafolder.");
            }
        }

        //Setting up Config
        config = new Config(this, this.getDataFolder(), "config", true, true);
        this.getLogger().info("Setting up the config.yml...");

        // todo: Initialize runnable that checks every second for expired trades
        // A better way to do this may be checking when the next expiration will be and run the runnable at that time
        // On loading the database, all trades past the expiry time need to be removed
        // Expiry time should be specified in the config file

        // Initialize managers
        storeManager = new StoreManager(this);
        playerManager = new PlayerManager(this);

        // Setting up listeners

        // GUI Listeners
        Bukkit.getPluginManager().registerEvents(new ItemBuyerScreenListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemSellerScreenListener(), this);
        Bukkit.getPluginManager().registerEvents(new MainShopScreenListener(), this);
        Bukkit.getPluginManager().registerEvents(new StoreUpdateScreenListener(), this);
        Bukkit.getPluginManager().registerEvents(new TradeManagementScreenListener(), this);
        Bukkit.getPluginManager().registerEvents(new TradeRemovalScreenListener(), this);
        Bukkit.getPluginManager().registerEvents(new TradeViewerScreenListener(), this);
        Bukkit.getPluginManager().registerEvents(new ViewCompletedTradesScreenListener(), this);
        Bukkit.getPluginManager().registerEvents(new ViewCurrentListingsScreenListener(), this);

        // Setting up commands
        try {
            Objects.requireNonNull(getCommand("worldshop")).setExecutor(new WorldshopCommand());
        } catch (NullPointerException exception) {
            Logger.logStacktrace(exception);
        }
    }

    // Config accessor
    public static Config getConfiguration() {
        return config;
    }

    public static StoreManager getStoreManager() {
        return storeManager;
    }

    // Player Manager accessor
    public static PlayerManager getPlayerManager() {
        return playerManager;
    }


    @Override
    public void onDisable() {

    }
}
