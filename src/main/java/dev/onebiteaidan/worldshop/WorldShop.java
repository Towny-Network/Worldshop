package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.Controller.Commands.WorldshopCommand;
import dev.onebiteaidan.worldshop.Controller.Listeners.TradeListener;
import dev.onebiteaidan.worldshop.Controller.PlayerManager;
import dev.onebiteaidan.worldshop.Model.DataManagement.Config;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database;
import dev.onebiteaidan.worldshop.Model.DataManagement.MySQL;
import dev.onebiteaidan.worldshop.Model.DataManagement.SQLite;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class WorldShop extends JavaPlugin {

    private static Config config;
    private static Database database;
    private static PlayerManager playerManager;


    @Override
    public void onEnable() {
        // Checks if data folder exists
        if (!getDataFolder().exists()) {
            this.getLogger().info("Datafolder for WorldShop Does not Exist. Creating now...");
            getDataFolder().mkdirs();
        }

        //Setting up Config
        config = new Config(this, this.getDataFolder(), "config", true, true);
        this.getLogger().info("Setting up the config.yml...");

        //Setting up database
        switch(Config.getDatabaseType()) {
            case "SQLite":
                database = new SQLite("worldshop.db");
                break;

            case "MySQL":
                database = new MySQL();
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

                // Initialize the players table if it doesn't exist
                database.createPlayersTable();

                // Initialize the shop table if it doesn't exit
                database.createTradesTable();

                // Initialize a rewards pickup table
                database.createPickupsTable();

        } else {
            this.getLogger().severe("UNABLE TO CONNECT TO THE DATABASE!");
            // Disables the plugin if the database doesn't connect.
            this.onDisable();
        }

        // todo: Initialize runnable that checks every second for expired trades
        // A better way to do this may be checking when the next expiration will be and run the runnable at that time
        // On loading the database, all trades past the expiry time need to be removed
        // Expiry time should be specified in the config file

        // Initializing the player manager
        playerManager = new PlayerManager();

        // Setting up listeners
        // All GUIs handle their own listener creation
        Bukkit.getPluginManager().registerEvents(new TradeListener(), this);

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

    // Player Manager accessor
    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public void onDisable() {
        // Close connection from database.
        database.disconnect();
    }
}
