package dev.onebiteaidan.worldshop.Model.DataManagement.Database;

import dev.onebiteaidan.worldshop.Utils.Logger;

import java.io.File;
import java.sql.*;

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

    public Connection getConnection() {
        return connection;
    }
}
