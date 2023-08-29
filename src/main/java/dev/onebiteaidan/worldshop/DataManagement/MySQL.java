package dev.onebiteaidan.worldshop.DataManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.onebiteaidan.worldshop.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.sql.*;

public class MySQL implements Database {

    private final String HOST = Config.getHost();
    private final int PORT = Config.getPort();
    private final String DATABASE = Config.getDatabase();
    private final String USERNAME = Config.getUsername();
    private final String PASSWORD = Config.getPassword();

    private HikariDataSource hikari;

    public MySQL() {

    }

    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + HOST + ":" + PORT);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        // Other configuration options like maximumPoolSize, connectionTimeout, etc.

        hikari = new HikariDataSource(config);


        //FIXME: Database creates database if non-existent but will not use it

        if (this.isConnected()) {
            this.run("CREATE DATABASE IF NOT EXISTS " + DATABASE + ";");
            this.run("USE " + DATABASE + ";");
        }
    }

    public boolean isConnected() {
        if (hikari != null) {
            try {
                return hikari.getConnection() != null;
            } catch(SQLException e) {
                return false;
            }
        }
        return false;
    }

    public void disconnect() {
        if (this.isConnected()) {
            hikari.close();
        }
    }

    public Connection getConnection() {
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // THIS QUERY FUNCTION IS MADE SPECIFICALLY FOR WORLDSHOP.
    // THIS WILL NOT WORK OUT OF THE BOX IN OTHER JAVA PROJECTS.
    @Override
    public ResultSet query(String query, Object[] arguments, int[] types) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
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
                        ps.setString(i + 1, (String) arguments[i]);
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
            PreparedStatement ps = getConnection().prepareStatement(update);
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
                        ps.setString(i + 1, (String) arguments[i]);
                        break;
                    default:
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String command) {
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement(command)) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createDatabase(String name) {
        this.run("CREATE DATABASE IF NOT EXISTS " + name + ";");
        this.run("USE " + name + ";");
    }

    @Override
    public void createTradesTable() {
        this.run("USE " + DATABASE + ";");
        this.run(  // Storing items in mysql https://www.spigotmc.org/threads/ways-to-storage-a-inventory-to-a-database.547207/
                "CREATE TABLE IF NOT EXISTS trades" +
                        "(" +
                        "trade_id int UNIQUE AUTO_INCREMENT," +
                        "status int," + // ENUM statuses include: OPEN, COMPLETE, EXPIRED, REMOVED
                        "seller_uuid varchar(36)," + // The length of a UUID will never be longer than 36 characters
                        "buyer_uuid varchar(36)," +
                        "for_sale BLOB," +  // Itemstacks can be stored in the BLOB datatype after being converted to byte arrays
                        "in_return BLOB," + // Barter item (the item someone will get in return) will also have to be stored as byte arrays
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
