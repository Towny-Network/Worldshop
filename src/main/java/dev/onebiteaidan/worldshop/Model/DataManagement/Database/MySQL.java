package dev.onebiteaidan.worldshop.Model.DataManagement.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.onebiteaidan.worldshop.Model.DataManagement.Config;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;
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
        config.setMaximumPoolSize(1000);
        // Other configuration options like maximumPoolSize, connectionTimeout, etc.

        hikari = new HikariDataSource(config);

        if (this.isConnected()) {
            // First, create the database if it doesn't exist
            this.run("CREATE DATABASE IF NOT EXISTS " + DATABASE + ";");

            // Then, close the existing connection to create a new one to the specific database
            hikari.close();

            // Reconfigure HikariCP to connect to the specific database
            config.setJdbcUrl("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE);
            hikari = new HikariDataSource(config);
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

    public ResultSet query(String query, Object[] arguments, int[] types, Connection connection) {
//        if (!this.isConnected()) {
//            this.connect();
//        }
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
                    case Types.NULL:
                        ps.setNull(i + 1, Types.NULL);
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


    public void update(String update, Object[] arguments, int[] types) {
//        if (!this.isConnected()) {
//            this.connect();
//        }
        try (Connection connection = hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(update);
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
                    case Types.NULL:
                        ps.setNull(i + 1, Types.NULL);
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

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void run(String command) {
//        if (!this.isConnected()) {
//            this.connect();
//        }
        try (Connection connection = hikari.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(command);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTradesTable() {
        this.run(  // Storing items in mysql https://www.spigotmc.org/threads/ways-to-storage-a-inventory-to-a-database.547207/
                "CREATE TABLE IF NOT EXISTS trades" +
                        "(" +
                        "trade_id int PRIMARY KEY AUTO_INCREMENT," +
                        "trade_status status ENUM('OPEN', 'COMPLETE', 'EXPIRED', 'REMOVED') DEFAULT 'OPEN'," +
                        "seller_uuid varchar(36)," + // The length of a UUID will never be longer than 36 characters
                        "buyer_uuid varchar(36)," +
                        "for_sale BLOB," +  // Itemstacks can be stored in the BLOB datatype after being converted to byte arrays
                        "in_return BLOB," + // Barter item (the item someone will get in return) will also have to be stored as byte arrays
                        "time_listed TIMESTAMP," +
                        "time_completed TIMESTAMP" +
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
