package dev.onebiteaidan.worldshop.Model.DataManagement.Database;

import java.io.File;
import java.sql.*;
import java.util.List;

public class SQLite implements Database {

    private final File filePath;
    private Connection connection;

    /**
     * Creates an MySQL Database object.
     * @param filePath Path to the database file. Assumes the file exists.
     */
    public SQLite(File filePath) {
        this.filePath = filePath;
    }

    /**
     * Connect to the SQLite database.
     * @throws SQLException if an error occurs when attempting to connect.
     */
    public void connect() throws SQLException {
        String connectionString = "jdbc:sqlite:" + this.filePath;
        connection = DriverManager.getConnection(connectionString);
    }

    /**
     * Disconnects to the SQLite database.
     * @throws SQLException if an error occurs when closing the connection or if there is no connection to be closed.
     */
    public void disconnect() throws SQLException {
        connection.close();
    }

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return this.connection;
    }


    public ResultSet executeQuery(String command, List<Object> parameters) throws SQLException {
        try (PreparedStatement ps = buildPreparedStatement(command, parameters)) {
            return ps.executeQuery();
        }
    }

    public int executeUpdate(String command, List<Object> parameters) throws SQLException {
        try (PreparedStatement ps = buildPreparedStatement(command, parameters)) {
            return ps.executeUpdate();
        }
    }

    public boolean execute(String command, List<Object> parameters) throws SQLException {
        try (PreparedStatement ps = buildPreparedStatement(command, parameters)) {
            return ps.execute();
        }
    }

    private PreparedStatement buildPreparedStatement(String command, List<Object> parameters) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement(command);

        // Set parameters
        for (int i = 0; i < parameters.size(); i++) {
            ps.setObject(i + 1, parameters.get(i));
        }

        return ps;
    }
}
