package dev.onebiteaidan.worldshop.DataManagement;

import dev.onebiteaidan.worldshop.WorldShop;

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

    @Override
    public ResultSet query(String query) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(String update) {
        try {
            PreparedStatement statement = connection.prepareStatement(update);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(String insertion) {
        try {
            PreparedStatement statement = connection.prepareStatement(insertion);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String deletion) {
        try {
            PreparedStatement statement = connection.prepareStatement(deletion);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String command) {
        try {
            PreparedStatement statement = connection.prepareStatement(command);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
