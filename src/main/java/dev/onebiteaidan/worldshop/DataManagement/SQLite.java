package dev.onebiteaidan.worldshop.DataManagement;

import dev.onebiteaidan.worldshop.WorldShop;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class SQLite implements Database {

    private String filename;

    private Connection connection;

    /**
     * Creates an SQLite database if one with filename doesn't already exist.
     * @param filename The filename for the database is in the format <name>.db
     */
    public void createIfDoesntExist(String filename) {
        String url = "jdbc:sqlite:/" + filename;

        // Check if the database exists file exists
        File f = new File(filename);
        if (!f.exists()) {
            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    WorldShop.getPlugin(WorldShop.class).getLogger().info("WorldShop Has created a new SQLite Database called '" + filename + "'");
                }
            } catch (SQLException e) {
                WorldShop.getPlugin(WorldShop.class).getLogger().severe("WorldShop Encountered an error while trying to create an SQLite Database with the name '"+ filename + "'!!!");
                e.printStackTrace();
            }
        } else {
            WorldShop.getPlugin(WorldShop.class).getLogger().info("WorldShop found preexisting database " + filename);
        }
    }

    public void connect() throws SQLException {
        createIfDoesntExist("shop.db");
        connection = DriverManager.getConnection(
                "jdbc:sqlite:/" + filename);
    }

    public boolean isConnected() {
        return false;
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
