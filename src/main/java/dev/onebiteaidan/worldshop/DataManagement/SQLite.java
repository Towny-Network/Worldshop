package dev.onebiteaidan.worldshop.DataManagement;

import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

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
    @Override
    public ResultSet query(String query, Object[] arguments, int[] types) {
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


    @Override
    public void update(String update, Object[] arguments, int[] types) {
        try {
            PreparedStatement ps = connection.prepareStatement(update);
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

    @Override
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
                "CREATE TABLE IF NOT EXISTS trades" +
                        "(" +
                        "trade_id INTEGER PRIMARY KEY," +
                        "seller_uuid varchar(36)," + // The length of a UUID will never be longer than 36 characters
                        "for_sale BLOB," +  // Itemstacks can be stored in the BLOB datatype after being converted to byte arrays
                        "in_return BLOB," + // Barter item (the item someone will get in return) will also have to be stored as byte arrays
                        "status int," + // ENUM statuses include: OPEN, COMPLETE, EXPIRED, REMOVED
                        "buyer_uuid varchar(36)," +
                        "time_listed BIGINT," +
                        "time_completed BIGINT" +
                        ");"
        );
    }

    @Override
    public void createPickupsTable() {
        this.run(
                "CREATE TABLE IF NOT EXISTS pickups" +
                        "(" +
                        "player_uuid varchar(36)," +
                        "pickup_item BLOB," +
                        "trade_id int," +
                        "collected boolean," +
                        "time_collected BIGINT" +
                        ");"
        );
    }

    @Override
    public void createPlayersTable() {
        this.run(
                "CREATE TABLE IF NOT EXISTS players" +
                        "(" +
                        "uuid varchar(36) UNIQUE," + // The length of a UUID will never be longer than 36 characters and will always be unique
                        "purchases int DEFAULT 0," + // Number of purchases
                        "sales int DEFAULT 0" + // Number of sales
                        ");"
        );
    }
}
