package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.Commands.TestCommand;
import dev.onebiteaidan.worldshop.DataManagement.Config;
import dev.onebiteaidan.worldshop.DataManagement.Database;
import dev.onebiteaidan.worldshop.DataManagement.MySQL;
import dev.onebiteaidan.worldshop.DataManagement.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class WorldShop extends JavaPlugin {

    private static Config config;
    private static Database database;
    private static StoreManager storeManager;

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
        switch(Config.getDatabaseType()) {
            case "SQLite":
                database = new SQLite("shop.db");
                break;

            case "MySQL":
                database = new MySQL();
                break;

            default: // Disables the plugin if the database cannot be initialized.
                this.getLogger().severe("Database could not be initialized because '" + Config.getDatabaseType() + "' is an INVALID database type!!!");
                this.onDisable();
        }

        try {
            database.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            this.onDisable();
        }

        if (database.isConnected()) {
            this.getLogger().info("WorldShop connected to its database successfully!");

                // Initialize the players table if it doesn't exist
                database.update(
                        "CREATE TABLE IF NOT EXISTS players" +
                                "(" +
                                "uuid varchar(36)," + // The length of a UUID will never be longer than 36 characters
                                "purchases int," + //Todo: Implement the system for tracking purchases and sales
                                "sales int," +
                                "CONSTRAINT players_constraint UNIQUE (uuid)" + // Makes it so a UUID of each player cannot repeat in this table
                                ");"
                );

                // Initialize the shop table if it doesn't exit
                database.update(  // Storing items in mysql https://www.spigotmc.org/threads/ways-to-storage-a-inventory-to-a-database.547207/
                        "CREATE TABLE IF NOT EXISTS trades" +
                                "(" +
                                "trade_id int," +
                                "seller_uuid varchar(36)," + // The length of a UUID will never be longer than 36 characters
                                "display_item BLOB," +
                                "for_sale BLOB," +  // Itemstacks can be stored in the BLOB datatype after being converted to byte arrays
                                "wanted BLOB," + // Barter item (the item someone will get in return) will also have to be stored as byte arrays
                                "num_wanted int," +
                                "status int," + // ENUM statuses include: OPEN, COMPLETE, EXPIRED, REMOVED
                                "buyer_uuid varchar(36)," +
                                "time_listed BIGINT," +
                                "time_completed BIGINT" +
                                ");"
                );

                // Initialize a rewards pickup table
                database.update(
                        "CREATE TABLE IF NOT EXISTS pickup" +
                                "(" +
                                "player_uuid varchar(36)," +
                                "trade_id int," +
                                "pickup_item BLOB," +
                                "collected boolean," +
                                "time_collected BIGINT" +
                                ");"
                );

        } else {
            this.getLogger().severe("WorldShop DID NOT SUCCESSFULLY CONNECT TO ITS DATABASE!!!");
        }

        // Initializing the store manager
        storeManager = new StoreManager();

        // Setting up listeners
        Bukkit.getPluginManager().registerEvents(new StoreListener(), this);

        // Setting up commands
        getCommand("test").setExecutor(new TestCommand());

    }

    // Config accessor
    public static Config getConfiguration() {
        return config;
    }

    // Database accessor
    public static Database getDatabase() {
        return database;
    }

    // Store Manager accessor
    public static StoreManager getStoreManager() {
        return storeManager;
    }

    @Override
    public void onDisable() {
        // Close connection from database.
        database.disconnect();
    }
}
