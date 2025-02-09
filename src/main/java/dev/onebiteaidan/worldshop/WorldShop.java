package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.Commands.WorldshopCommand;
import dev.onebiteaidan.worldshop.DataManagement.Config;
import dev.onebiteaidan.worldshop.DataManagement.Repositories.PickupRepository;
import dev.onebiteaidan.worldshop.DataManagement.Repositories.ProfileRepository;
import dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLitePickupRepository;
import dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteProfileRepository;
import dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteTradeRepository;
import dev.onebiteaidan.worldshop.DataManagement.Repositories.TradeRepository;
import dev.onebiteaidan.worldshop.Listeners.ScreenListeners.*;
import dev.onebiteaidan.worldshop.Utils.Logger;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class WorldShop extends JavaPlugin {

    private static Config config;
    private static StoreManager storeManager;
    private static PlayerManager playerManager;
    private Connection connection;


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

        // Load the Database file
        ProfileRepository profileRepository;
        TradeRepository tradeRepository;
        PickupRepository pickupRepository;

        switch(Config.getDatabaseType()) {
            case "SQLite":
                File databaseFile = new File(this.getDataFolder(), "worldshop.db");
                if (!databaseFile.exists()) {
                    try {
                        databaseFile.createNewFile(); // Create the file
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create database file!", e);
                    }
                }

                try {
                    // Store a persistent connection
                    this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

                    System.out.println("SQLite connection established.");

                    // Pass the same connection to all repositories
                    profileRepository = new SQLiteProfileRepository(connection);
                    tradeRepository = new SQLiteTradeRepository(connection);
                    pickupRepository = new SQLitePickupRepository(connection);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to connect to SQLite database!", e);
                }

                break;

            case "MySQL":
                throw new NotImplementedException("Database type 'MySQL' is not yet supported.");

            default:
                throw new RuntimeException("Database with type " + Config.getDatabaseType() + " is invalid!");
        }

        // todo: Initialize runnable that checks every second for expired trades
        // A better way to do this may be checking when the next expiration will be and run the runnable at that time
        // On loading the database, all trades past the expiry time need to be removed
        // Expiry time should be specified in the config file

        // Initialize managers
        storeManager = new StoreManager(tradeRepository, pickupRepository);
        playerManager = new PlayerManager(profileRepository);

        // Setting up listeners

        // GUI Listeners
//        Bukkit.getPluginManager().registerEvents(new ItemBuyerScreenListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemSellerScreenListener(), this);
//        Bukkit.getPluginManager().registerEvents(new MainShopScreenListener(), this);
//        Bukkit.getPluginManager().registerEvents(new StoreUpdateScreenListener(), this);
//        Bukkit.getPluginManager().registerEvents(new TradeManagementScreenListener(), this);
//        Bukkit.getPluginManager().registerEvents(new TradeRemovalScreenListener(), this);
//        Bukkit.getPluginManager().registerEvents(new TradeViewerScreenListener(), this);
//        Bukkit.getPluginManager().registerEvents(new ViewCompletedTradesScreenListener(), this);
//        Bukkit.getPluginManager().registerEvents(new ViewCurrentListingsScreenListener(), this);

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
        if (connection != null) {
            try {
                connection.close();
                System.out.println("SQLite connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
