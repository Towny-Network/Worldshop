package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.DataManagement.Config;
import dev.onebiteaidan.worldshop.DataManagement.Database;
import dev.onebiteaidan.worldshop.DataManagement.MySQL;
import dev.onebiteaidan.worldshop.DataManagement.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class WorldShop extends JavaPlugin {

    private static Config config;
    private static Database database;

    @Override
    public void onEnable() {
        // Checks if data folder exists
        if (!getDataFolder().exists()) {
            this.getLogger().info("Datafolder for WorldShop Does not Exist. Creating now...");
            getDataFolder().mkdirs();
        }

        //Setting up Config
        config = new Config(this, this.getDataFolder(), "config", true, true);

        //Setting up database
        switch(Config.getType()) {
            case "SQLite":
                database = new SQLite();
                break;

            case "MySQL":
                database = new MySQL();
                break;

            default:
                this.getLogger().severe("Database could not be initialized because '" + Config.getType() + "' is an INVALID database type!!!");
        }

        try {
            database.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            //Todo: add custom stuff here
        }

        if (database.isConnected()) {
            this.getLogger().info("WorldShop connected to its database successfully!");

            // Initialize database with tables if they don't exist
            try {
                PreparedStatement players_setup = database.getConnection().prepareStatement(
                        "CREATE TABLE IF NOT EXISTS players" +
                                "(" +
                                "id int NOT NULL AUTO_INCREMENT," +
                                "uuid varchar(36) NOT NULL," + // The length of a UUID will never be longer than 36 characters
                                "purchases int NOT NULL," +
                                "sales int NOT NULL" +
                                "CONSTRAINT players_constraint UNIQUE (uuid)" +
                                ")");

                players_setup.execute

                PreparedStatement shop_setup = database.getConnection().prepareStatement(
                        "CREATE TABLE IF NOT EXISTS shop" +
                                "id int NOT NULL AUTO_INCREMENT," +
                                "uuid varchar(36) NOT NULL," +
                                ""
                )

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            this.getLogger().severe("WorldShop DID NOT SUCCESSFULLY CONNECT TO ITS DATABASE!!!");
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

    @Override
    public void onDisable() {
        // Close connection from database.
        database.disconnect();
    }
}
