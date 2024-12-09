package dev.onebiteaidan.worldshop.Model.DataManagement.Database;

import dev.onebiteaidan.worldshop.Utils.Logger;

import java.io.File;
import java.sql.*;
import java.util.Queue;

public class SQLiteDatabase implements Database {

    private final File filePath;
    private Connection connection;

    /**
     * Creates an MySQL Database object.
     * @param filePath Path to the database file. Assumes the file exists.
     */
    public SQLiteDatabase(File filePath) {
        this.filePath = filePath;
    }

    /**
     * Connect to the SQLite database.
     */
    public void connect() {
        try {
            // Attempt to connect to the database
            String connectionString = "jdbc:sqlite:" + this.filePath;
            connection = DriverManager.getConnection(connectionString);
            Logger.info("Successfully connected to the SQLite Database");

            // Initialize the Database tables if they do not exist.
            initializeTradesTable();
            initializePickupsTable();
            initializePlayersTable();
            Logger.info("Successfully initialized SQLite tables.");

        } catch (SQLException e) {
            Logger.severe("Failed to connect to the SQLite Database");
            Logger.logStacktrace(e);
        }
    }

    /**
     * Disconnects to the SQLite database.
     */
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            Logger.severe("SQL Exception when disconnecting from SQLite database!");
            Logger.logStacktrace(e);
        }

    }

    public boolean isConnected() {
        return connection != null;
    }

    public boolean put(Object key, Object value) {
        if (Object instanceof Trade)
    }

    public Object get(Object key) {

    }

    private ResultSet executeQuery(String command, Object[] parameters) throws SQLException {
        // Cannot be run with try-with resources because the ResultSet object will close as soon as this function returns.
        PreparedStatement ps = buildPreparedStatement(command, parameters);
        return ps.executeQuery();

    }

    private int executeUpdate(String command, Object[] parameters) throws SQLException {
        try (PreparedStatement ps = buildPreparedStatement(command, parameters)) {
            return ps.executeUpdate();
        }
    }

    private boolean execute(String command, Object[] parameters) throws SQLException {
        try (PreparedStatement ps = buildPreparedStatement(command, parameters)) {
            return ps.execute();
        }
    }

    private PreparedStatement buildPreparedStatement(String command, Object[] parameters) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement(command);

        // Set parameters
        for (int i = 0; i < parameters.length; i++) {
            ps.setObject(i + 1, parameters[i]);
        }

        return ps;
    }

    private void initializeTradesTable() throws SQLException {
        this.execute("CREATE TABLE IF NOT EXISTS TRADES" +
                "(" +
                "TRADE_ID UNIQUE AUTO_INCREMENT," +
                "SELLER_UUID varchar(36)," +
                "BUYER_UUID varchar(36)," +
                "ITEM_OFFERED BLOB," +
                "ITEM_REQUESTED BLOB," +
                "TRADE_STATUS int," +
                "LISTING_TIMESTAMP BIGINT," +
                "COMPLETION_TIMESTAMP BIGINT" +
                ");"
        , null);
    }

    private void initializePickupsTable() throws SQLException {
        this.execute("CREATE TABLE IF NOT EXISTS PICKUPS" +
                "(" +
                "PLAYER_UUID varchar(36)," +
                "PICKUP_ITEM BLOB," +
                "COLLECTED boolean," +
                "TIME_COLLECTED BIGINT" +
                ");"
        , null);
    }

    private void initializePlayersTable() throws SQLException {
        this.execute("CREATE TABLE IF NOT EXISTS PLAYERS" +
                "(" +
                "PLAYER_UUID varchar(36)," +
                "PURCHASES int," +
                "SALES int" +
                ");"
        , null);
    }
}
