package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.Utils.Logger;
import org.apache.commons.lang3.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public abstract class TradeRepository<K, V> implements Repository<K, V> {

    protected Connection database;

    public TradeRepository(File filePath) {

        // Check if the database file exists
        try {
            if (filePath.createNewFile()) {
                Logger.info("Created a new SQLite database file at " + filePath);
            } else {
                Logger.info("Pre-existing SQLite database file found ");
            }
        } catch (IOException e) {
            Logger.severe("Error encountered while creating an SQLite database file!");
            Logger.logStacktrace(e);
        }

        // Attempt to connect to the SQLite database via the JDBC driver
        try {
            String connectionString = "jdbc:sqlite:" + filePath;
            this.database = DriverManager.getConnection(connectionString);
            Logger.info("Successfully connected to the SQLite Database");

        } catch (SQLException e) {
            Logger.severe("Failed to connect to the SQLite Database");
            Logger.logStacktrace(e);
        }

        initializeTable();
    }

    protected abstract void initializeTable();

    public void delete(K id) {
        throw new NotImplementedException("Delete function is not implemented in SQLiteRepository!");
    }

//    private void initializeTable() throws SQLException {
//        database.execute("CREATE TABLE IF NOT EXISTS TRADES" +
//                        "(" +
//                        "TRADE_ID UNIQUE AUTO_INCREMENT," +
//                        "SELLER_UUID varchar(36)," +
//                        "BUYER_UUID varchar(36)," +
//                        "ITEM_OFFERED BLOB," +
//                        "ITEM_REQUESTED BLOB," +
//                        "TRADE_STATUS int," +
//                        "LISTING_TIMESTAMP BIGINT," +
//                        "COMPLETION_TIMESTAMP BIGINT" +
//                        ");"
//                , null);
//    }
//
//    private void initializePickupsTable() throws SQLException {
//        this.execute("CREATE TABLE IF NOT EXISTS PICKUPS" +
//                        "(" +
//                        "PLAYER_UUID varchar(36)," +
//                        "PICKUP_ITEM BLOB," +
//                        "COLLECTED boolean," +
//                        "TIME_COLLECTED BIGINT" +
//                        ");"
//                , null);
//    }
//
//    private void initializePlayersTable() throws SQLException {
//        this.execute("CREATE TABLE IF NOT EXISTS PLAYERS" +
//                        "(" +
//                        "PLAYER_UUID varchar(36)," +
//                        "PURCHASES int," +
//                        "SALES int" +
//                        ");"
//                , null);
//    }




}
