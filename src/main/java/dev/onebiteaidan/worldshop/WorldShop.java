package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.Controller.Commands.WorldshopCommand;
import dev.onebiteaidan.worldshop.Controller.Listeners.PickupListener;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners.*;
import dev.onebiteaidan.worldshop.Controller.Listeners.TradeListener;
import dev.onebiteaidan.worldshop.Controller.PlayerManager;
import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.Model.DataManagement.Config;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.SQLite;
import dev.onebiteaidan.worldshop.Utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public final class WorldShop extends JavaPlugin {

    private static Config config;
    private static Database database;
    private static StoreManager storeManager;
    private static PlayerManager playerManager;

    @Override
    public void onEnable() {
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

        //Setting up database
        switch(Config.getDatabaseType()) {
            case "SQLite":
                File databaseFile = new File(this.getDataFolder(), "worldshop.db");

                // Create the database file if it does not exist.
                if (!databaseFile.exists()) {
                    try {
                        if (databaseFile.createNewFile()) {
                            Logger.info("Database file created: " + databaseFile.getAbsolutePath());
                        } else {
                            Logger.severe("Failed to create the database file.");
                        }
                    } catch (IOException e) {
                        Logger.logStacktrace(e);
                    }
                }

                // Create the database object.
                database = new SQLite(databaseFile);
                break;

            default: // Disables the plugin if the database cannot be initialized.
                this.getLogger().severe("DATABASE COULD NOT BE INITIALIZED BECAUSE '" + Config.getDatabaseType() + "' IS AN INVALID DATABASE TYPE");
                this.onDisable();
        }

        try {
            database.connect();
        } catch (Exception exception) { // Disable the plugin if the database throws exception while trying to connect.
            Logger.logStacktrace(exception);
            this.getLogger().severe("ERROR THROWN WHILE CONNECTING TO THE DATABASE!");
            this.onDisable();
        }


        if (database.isConnected()) {
            this.getLogger().info("Connected to its database successfully!");

            // No database initialization required here. Handled by the caches initialized in PlayerManager and TradeManager.

        } else {
            this.getLogger().severe("UNABLE TO CONNECT TO THE DATABASE!");
            // Disables the plugin if the database doesn't connect.
            this.onDisable();
        }

        // todo: Initialize runnable that checks every second for expired trades
        // A better way to do this may be checking when the next expiration will be and run the runnable at that time
        // On loading the database, all trades past the expiry time need to be removed
        // Expiry time should be specified in the config file

        // Initialize managers
        storeManager = new StoreManager();
        playerManager = new PlayerManager();

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

        Bukkit.getPluginManager().registerEvents(new TradeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PickupListener(), this);

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

    // Database accessor
    public static Database getDatabase() {
        return database;
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
        // Close connection from database.
        try {
            database.disconnect();
        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }
    }
}
