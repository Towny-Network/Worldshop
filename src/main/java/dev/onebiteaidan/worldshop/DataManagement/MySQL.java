package dev.onebiteaidan.worldshop.DataManagement;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;

public class MySQL implements Database {

    private final String HOST = Config.getHost();
    private final int PORT = Config.getPort();
    private final String DATABASE = Config.getDatabase();
    private final String USERNAME = Config.getUsername();
    private final String PASSWORD = Config.getPassword();

    private HikariDataSource hikari;

    public void connect() {
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", HOST);
        hikari.addDataSourceProperty("port", PORT);
        hikari.addDataSourceProperty("databaseName", DATABASE);
        hikari.addDataSourceProperty("user", USERNAME);
        hikari.addDataSourceProperty("password", PASSWORD);
    }

    public boolean isConnected() {
        return hikari != null;
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

    @Override
    public ResultSet query(String query) {
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(String update) {
        try (Connection connection = hikari.getConnection();
            PreparedStatement statement = connection.prepareStatement(update)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(String insertion) {
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertion)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String deletion) {
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement(deletion)) {
            statement.executeUpdate();
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
}
