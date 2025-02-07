package dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Profile;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.sql.*;
import java.util.*;

import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteProfileSchema.Column.*;
import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteProfileSchema.PROFILES_INIT_COMMAND;
import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteProfileSchema.PROFILES_TABLE;
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

    @Test
    void constructorShouldSkipInitializationIfTableAlreadyExists() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            // Populate table with the schema
            PreparedStatement ps = connection.prepareStatement(PROFILES_INIT_COMMAND);
            ps.execute();

        } catch (SQLException e) {
            fail("SQLException Occurred During test initialization process (test case issue): " + e.getMessage());
        }

        // Close and reopen with another try with block
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            // Initialize the repository
            SQLiteProfileRepository repository = new SQLiteProfileRepository(connection);

            // Verify the TRADES table was created
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
            fail("SQLException thrown during test: " + e.getMessage());
        }
    }

    @Nested
    class TestWithCorrectDatabaseInitialization {
        ServerMock server;
        SQLiteProfileRepository repository;

        @BeforeEach
        public void setUp() throws SQLException {
            server = MockBukkit.mock();
            repository = new SQLiteProfileRepository(DriverManager.getConnection("jdbc:sqlite::memory:"));
        }

        @AfterEach
        public void tearDown() {
            MockBukkit.unmock();
        }

        @Nested
        class TestFindById {
            @Test
            void findByIdShouldReturnSameProfileAsSaved() {
                PlayerMock playerMock = server.addPlayer();
                UUID playerUUID = playerMock.getUniqueId();
                playerMock.disconnect();

                OfflinePlayer player = server.getOfflinePlayer(playerUUID);

                Profile profile = new Profile(player, 1005, 2);

                // Save trade to database
                repository.save(profile);

                // Retrieve the profile
                Profile profileRetrieved = repository.findById(player.getUniqueId());

                // Compare
                assertEquals(profile, profileRetrieved);
            }

            @Test
            void findByIdWithMissingIDShouldReturnNull() {
                // Retrieve the trade with the missing UUID (a random UUID)
                assertNull(repository.findById(UUID.randomUUID()));
            }
        }

        @Nested
        class TestFindAll {
            @Test
            void findAllShouldReturnAllProfiles() {
                PlayerMock player1Mock = server.addPlayer();
                PlayerMock player2Mock = server.addPlayer();

                UUID player1UUID = player1Mock.getUniqueId();
                UUID player2UUID = player2Mock.getUniqueId();

                player1Mock.disconnect();
                player2Mock.disconnect();

                OfflinePlayer player1 = server.getOfflinePlayer(player1UUID);
                OfflinePlayer player2 = server.getOfflinePlayer(player2UUID);

                Profile profile1 = new Profile(player1, 100, 5);
                Profile profile2 = new Profile(player2, 100, 5);

                repository.save(profile1);
                repository.save(profile2);

                List<Profile> profiles = repository.findAll();

                assertEquals(2, profiles.size());
                assertEquals(profiles.get(0), profile1);
                assertEquals(profiles.get(1), profile2);
            }

            @Test
            void findAllShouldReturnNoTrades() {
                List<Profile> profiles = repository.findAll();
                assertTrue(profiles.isEmpty());
            }
        }

        @Nested
        class TestSave {

            @Test
            void saveWithUUIDShouldUpdateObjectInDatabase() {
                // Prepopulate database with profile
                PlayerMock player1Mock = server.addPlayer();

                UUID player1UUID = player1Mock.getUniqueId();

                player1Mock.disconnect();

                OfflinePlayer player1 = server.getOfflinePlayer(player1UUID);

                Profile profile1 = new Profile(player1, 100, 5);

                repository.save(profile1);

                // Save trade to database
                repository.save(profile1);
                assertEquals(1, repository.findAll().size());

                // Retrieve save from database, modify it and save it.
                Profile profile2 = repository.findById(player1UUID);

                assertEquals(profile1, profile2);

                profile2.setSales(29);
                profile2.setPurchases(33339);

                repository.save(profile2);

                // Retrieve again and confirm that it modified the trade.
                Profile profile3 = repository.findById(player1UUID);

                assertEquals(profile2, profile3);

                assertEquals(1, repository.findAll().size());
            }
        }

        @Nested
        class TestDelete {
            @Test
            void delete() {
                Exception exception = assertThrows(Exception.class, () -> repository.delete(UUID.randomUUID()));
                assertEquals("DELETE feature not available in the ProfileRepository", exception.getMessage());
            }
        }

    }
}
