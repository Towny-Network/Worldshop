package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite;

import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteProfileSchema.Column.*;
import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteProfileSchema.PROFILES_TABLE;
import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteTradeSchema.Column.*;
import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteTradeSchema.Column.COMPLETION_TIMESTAMP;
import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteTradeSchema.TRADES_TABLE;
import static org.junit.jupiter.api.Assertions.*;

public class SQLiteProfileRepositoryTest {

    @Test
    void constructorShouldInitializeEmptyDatabaseWithPickupsTable() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            // Initialize the repository
            SQLiteProfileRepository repository = new SQLiteProfileRepository(connection);

            // Verify the PLAYERS table was created
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT name FROM sqlite_master WHERE type='table' AND name='" + PROFILES_TABLE + "';")) {

                assertTrue(resultSet.next(), "Table " + PROFILES_TABLE + " should be created.");
            }

            // Verify table columns
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + PROFILES_TABLE + ");")) {

                Map<String, String> columns = new HashMap<>();
                while (resultSet.next()) {
                    columns.put(resultSet.getString("name"), resultSet.getString("type"));
                }

                assertEquals(PLAYER_UUID.getColumnType(), columns.get(PLAYER_UUID.getColumnName()), PLAYER_UUID + " column type mismatch");
                assertEquals(PURCHASES.getColumnType(), columns.get(PURCHASES.getColumnName()), PURCHASES + " column type mismatch");
                assertEquals(SALES.getColumnType(), columns.get(SALES.getColumnName()), SALES + " column type mismatch");
            }

        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }
}
