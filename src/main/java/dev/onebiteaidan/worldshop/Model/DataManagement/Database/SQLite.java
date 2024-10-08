package dev.onebiteaidan.worldshop.Model.DataManagement.Database;

import dev.onebiteaidan.worldshop.WorldShop;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema.Table;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema.TradeColumn;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema.PlayerColumn;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema.PickupColumn;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLite implements Database {

    private final String filename;
    private Connection connection;

    public SQLite(String filename) {

        this.filename = filename;

        createIfDoesntExist(filename);
    }

    /**
     * Creates an SQLite database if one with filename doesn't already exist.
     * @param filename The filename for the database is in the format <name>.db
     */
    public void createIfDoesntExist(String filename) {
        // Check if the database file exists already
        File db = new File(WorldShop.getPlugin(WorldShop.class).getDataFolder(), filename);
        try {
            if (db.createNewFile()) {
                WorldShop.getPlugin(WorldShop.class).getLogger().info("WorldShop Has created a new SQLite Database File called '" + filename + "'");
            } else {
                WorldShop.getPlugin(WorldShop.class).getLogger().info("WorldShop found preexisting database " + filename);
            }
        } catch (IOException e) {
            WorldShop.getPlugin(WorldShop.class).getLogger().severe("WorldShop encountered an error while trying to create an SQLite Database with the name '"+ filename + "'!!!");
        }
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + new File(WorldShop.getPlugin(WorldShop.class).getDataFolder(), filename));
    }

    public boolean isConnected() {
        return connection != null;
    }
    
    public void disconnect() {
        try {
            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    public Connection getConnection() {
        return this.connection;
    }

    // THIS QUERY FUNCTION IS MADE SPECIFICALLY FOR WORLDSHOP.
    // THIS WILL NOT WORK OUT OF THE BOX IN OTHER JAVA PROJECTS.

    public ResultSet query(String query, Object[] arguments, int[] types, Connection connection) {
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 0; i < arguments.length; i++) {
                switch (types[i]) {
                    case Types.INTEGER:
                        ps.setInt(i + 1, (int) arguments[i]);
                        break;
                    case Types.BOOLEAN:
                        ps.setBoolean(i + 1, (boolean) arguments[i]);
                        break;
                    case Types.BIGINT:
                        ps.setLong(i + 1, (long) arguments[i]);
                        break;
                    case Types.BLOB:
                        ps.setBytes(i + 1, ((ItemStack) arguments[i]).serializeAsBytes());
                        break;
                    case Types.VARCHAR:
                        ps.setString(i + 1, arguments[i].toString());
                        break;
                    default:
                        break;
                }
            }
            return ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void update(String update, Object[] arguments, int[] types) {
        try (PreparedStatement ps = connection.prepareStatement(update)) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] == null) {
                    ps.setNull(i + 1, Types.NULL);
                    continue;
                }
                switch (types[i]) {
                    case Types.INTEGER:
                        ps.setInt(i + 1, (int) arguments[i]);
                        break;
                    case Types.BOOLEAN:
                        ps.setBoolean(i + 1, (boolean) arguments[i]);
                        break;
                    case Types.BIGINT:
                        ps.setLong(i + 1, (long) arguments[i]);
                        break;
                    case Types.NULL:
                        ps.setNull(i + 1, Types.NULL);
                        //todo: does this need a break statement?
                    case Types.BLOB:
                        ps.setBytes(i + 1, ((ItemStack) arguments[i]).serializeAsBytes());
                        break;
                    case Types.VARCHAR:
                        ps.setString(i + 1, arguments[i].toString());
                        break;
                    default:
                        break;
                }
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void run(String command) {
        try {
            PreparedStatement ps = connection.prepareStatement(command);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTradesTable() {
        this.run(  // Storing items in mysql https://www.spigotmc.org/threads/ways-to-storage-a-inventory-to-a-database.547207/
                "CREATE TABLE IF NOT EXISTS " + Table.TRADES +
                        "(" +
                        TradeColumn.TRADE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        TradeColumn.SELLER_UUID + " varchar(36)," + // The length of a UUID will never be longer than 36 characters
                        TradeColumn.BUYER_UUID + " varchar(36)," +
                        TradeColumn.ITEM_OFFERED + " BLOB," +  // Item stacks can be stored in the BLOB datatype after being converted to byte arrays
                        TradeColumn.ITEM_REQUESTED + " BLOB," + // Barter item (the item someone will get in return) will also have to be stored as byte arrays
                        TradeColumn.TRADE_STATUS + " int," + // ENUM statuses include: OPEN, COMPLETE, EXPIRED, REMOVED
                        TradeColumn.LISTING_TIMESTAMP + " BIGINT," +
                        TradeColumn.COMPLETION_TIMESTAMP + " BIGINT" +
                        ");"
        );
    }

    @Override
    public void createPickupsTable() {
        this.run(
                "CREATE TABLE IF NOT EXISTS " + Table.PICKUPS +
                        "(" +
                        PickupColumn.PLAYER_UUID + " varchar(36)," +
                        PickupColumn.PICKUP_ITEM + " BLOB," +
                        PickupColumn.TRADE_ID + " int," +
                        PickupColumn.COLLECTED + " boolean," +
                        PickupColumn.TIME_COLLECTED + " BIGINT" +
                        ");"
        );
    }

    @Override
    public void createPlayersTable() {
        this.run(
                "CREATE TABLE IF NOT EXISTS " + Table.PLAYERS +
                        "(" +
                        PlayerColumn.PLAYER_UUID + " varchar(36) UNIQUE," + // The length of a UUID will never be longer than 36 characters and will always be unique
                        PlayerColumn.PURCHASES + " int DEFAULT 0," + // Number of purchases
                        PlayerColumn.SALES + " int DEFAULT 0" + // Number of sales
                        ");"
        );
    }
}
