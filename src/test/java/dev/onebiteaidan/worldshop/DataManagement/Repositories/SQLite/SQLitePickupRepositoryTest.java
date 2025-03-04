package dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLitePickupSchema.*;
import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLitePickupSchema.Column.*;
import static org.junit.jupiter.api.Assertions.*;

public class SQLitePickupRepositoryTest {

    @Test
    void constructorShouldInitializeEmptyDatabaseWithPickupsTable() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            // Initialize the repository
            SQLitePickupRepository repository = new SQLitePickupRepository(connection);

            // Verify the PICKUPS table was created
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT name FROM sqlite_master WHERE type='table' AND name='" + PICKUPS_TABLE + "';")) {

                assertTrue(resultSet.next(), "Table " + PICKUPS_TABLE + " should be created.");
            }

            // Verify table columns
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + PICKUPS_TABLE + ");")) {

                Map<String, String> columns = new HashMap<>();
                while (resultSet.next()) {
                    columns.put(resultSet.getString("name"), resultSet.getString("type"));
                }

                assertEquals(PICKUP_ID.getColumnType(), columns.get(PICKUP_ID.getColumnName()), PICKUP_ID + " column type mismatch");
                assertEquals(PLAYER_UUID.getColumnType(), columns.get(PLAYER_UUID.getColumnName()), PLAYER_UUID + " column type mismatch");
                assertEquals(PICKUP_ITEM.getColumnType(), columns.get(PICKUP_ITEM.getColumnName()), PICKUP_ITEM + " column type mismatch");
                assertEquals(TRADE_ID.getColumnType(), columns.get(TRADE_ID.getColumnName()), TRADE_ID + " column type mismatch");
                assertEquals(COLLECTED.getColumnType(), columns.get(COLLECTED.getColumnName()), COLLECTED + " column type mismatch");
                assertEquals(COLLECTION_TIMESTAMP.getColumnType(), columns.get(COLLECTION_TIMESTAMP.getColumnName()), COLLECTION_TIMESTAMP + " column type mismatch");
            }

        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    @Test
    void constructorShouldSkipInitializationIfTableAlreadyExists() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            // Populate the table with the schema
            PreparedStatement ps = connection.prepareStatement(PICKUPS_INIT_COMMAND);
            ps.execute();

        } catch (SQLException e) {
            fail("SQLException Occurred During test initialization process (test case issue): " + e.getMessage());
        }

        // Close and reopen with another try with block
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            // Initialize the repository
            SQLitePickupRepository repository = new SQLitePickupRepository(connection);

            // Verify the TRADES table was created
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT name FROM sqlite_master WHERE type='table' AND name='" + PICKUPS_TABLE + "';")) {

                assertTrue(resultSet.next(), "Table " + PICKUPS_TABLE + " should be created.");
            }

            // Verify table columns
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + PICKUPS_TABLE + ");")) {

                Map<String, String> columns = new HashMap<>();
                while (resultSet.next()) {
                    columns.put(resultSet.getString("name"), resultSet.getString("type"));
                }

                assertEquals(PICKUP_ID.getColumnType(), columns.get(PICKUP_ID.getColumnName()), PICKUP_ID + " column type mismatch");
                assertEquals(PLAYER_UUID.getColumnType(), columns.get(PLAYER_UUID.getColumnName()), PLAYER_UUID + " column type mismatch");
                assertEquals(PICKUP_ITEM.getColumnType(), columns.get(PICKUP_ITEM.getColumnName()), PICKUP_ITEM + " column type mismatch");
                assertEquals(TRADE_ID.getColumnType(), columns.get(TRADE_ID.getColumnName()), TRADE_ID + " column type mismatch");
                assertEquals(COLLECTED.getColumnType(), columns.get(COLLECTED.getColumnName()), COLLECTED + " column type mismatch");
                assertEquals(COLLECTION_TIMESTAMP.getColumnType(), columns.get(COLLECTION_TIMESTAMP.getColumnName()), COLLECTION_TIMESTAMP + " column type mismatch");
            }

        } catch (SQLException e) {
            fail("SQLException thrown during test: " + e.getMessage());
        }
    }

    @Nested
    class TestWithCorrectDatabaseInitialization {
        ServerMock server;
        SQLitePickupRepository repository;

        @BeforeEach
        public void setUp() throws SQLException {
            server = MockBukkit.mock();

            // Ensure that Logger is initialized properly
            Plugin plugin = MockBukkit.load(WorldShop.class);

            repository = new SQLitePickupRepository(DriverManager.getConnection("jdbc:sqlite::memory:"));
        }

        @AfterEach
        public void tearDown() {
            MockBukkit.unmock();
        }

        @Nested
        class TestFindById {
            @Test
            void findByIdShouldReturnSamePickupAsInserted() {
                PlayerMock playerMock = server.addPlayer();
                UUID playerUUID = playerMock.getUniqueId();
                playerMock.disconnect();

                OfflinePlayer player = server.getOfflinePlayer(playerUUID);
                ItemStack item = new ItemStack(Material.DIAMOND_AXE);

                Pickup pickup = new Pickup(100, player, item, 210, false, 1L);

                // Save pickup to database
                repository.save(pickup);

                // Retrieve the trade
                Pickup pickupRetrieved = repository.findById(100);

                // Compare
                assertEquals(pickup, pickupRetrieved);
            }

            @Test
            void findByIdWithMissingIDShouldReturnNull() {
                // Retrieve the pickup with the missing ID 101
                assertNull(repository.findById(101));
            }

            @Test
            void findByIdWithInvalidIDShouldThrowIllegalArgumentException() {
                // Retrieve a trade with the pickupID of -1
                Exception exception = assertThrows(Exception.class, () -> repository.findById(-1));
                assertEquals("Pickup IDs must be greater than or equal to zero!", exception.getMessage());
            }
        }

        @Nested
        class TestFindAll {
            @Test
            void findAllShouldReturnAllPickups() {
                PlayerMock player1Mock = server.addPlayer();
                PlayerMock player2Mock = server.addPlayer();

                UUID player1UUID = player1Mock.getUniqueId();
                UUID player2UUID = player2Mock.getUniqueId();

                player1Mock.disconnect();
                player2Mock.disconnect();

                OfflinePlayer player1 = server.getOfflinePlayer(player1UUID);
                OfflinePlayer player2 = server.getOfflinePlayer(player2UUID);

                ItemStack item1 = new ItemStack(Material.OAK_PLANKS, 35);
                ItemStack item2 = new ItemStack(Material.DIAMOND_AXE);

                Pickup pickup1 = new Pickup(100, player1, item1, 2007, false, 0L);
                Pickup pickup2 = new Pickup(105, player2, item2, 35569, true, 1L);

                repository.save(pickup1);
                repository.save(pickup2);

                List<Pickup> pickups = repository.findAll();

                assertEquals(2, pickups.size());
                assertEquals(pickups.get(0), pickup1);
                assertEquals(pickups.get(1), pickup2);
            }

            @Test
            void findAllShouldReturnNoPickups() {
                List<Pickup> pickups = repository.findAll();
                assertTrue(pickups.isEmpty());
            }
        }

        @Nested
        class TestSave {

            @Test
            void saveWithIDShouldUpdateObjectInDatabase() {
                // Prepopulate database with pickup
                PlayerMock player1Mock = server.addPlayer();
                UUID player1UUID = player1Mock.getUniqueId();
                player1Mock.disconnect();

                OfflinePlayer player1 = server.getOfflinePlayer(player1UUID);
                ItemStack item1 = new ItemStack(Material.OAK_PLANKS, 35);

                Pickup pickup1 = new Pickup(999, player1, item1, 2007, false, 0L);

                // Save pickup to database
                repository.save(pickup1);
                assertEquals(1, repository.findAll().size());

                // Retrieve save from database, modify it and save it.
                Pickup pickup2 = repository.findById(999);

                assertEquals(pickup1, pickup2);

                pickup2.setCollectionStatus(true);
                pickup2.setCollectionTimestamp(20L);

                repository.save(pickup2);

                // Retrieve again and confirm that it modified the trade.
                Pickup pickup3 = repository.findById(999);

                assertEquals(pickup2, pickup3);

                assertEquals(1, repository.findAll().size());
            }

            @Test
            void saveWithoutIDShouldAddObjectToDatabase() {
                // Create trade with no ID
                PlayerMock player1Mock = server.addPlayer();
                UUID player1UUID = player1Mock.getUniqueId();
                player1Mock.disconnect();

                OfflinePlayer player1 = server.getOfflinePlayer(player1UUID);
                ItemStack item1 = new ItemStack(Material.OAK_PLANKS, 35);

                Pickup pickup1 = new Pickup(-1, player1, item1, 2007, false, 0L);

                // Save trade
                repository.save(pickup1);

                // Confirm it was given a trade ID
                List<Pickup> pickups = repository.findAll();

                assertEquals(1, pickups.size());
                assertEquals(0, pickups.get(0).getPickupID());
            }

            @Test
            void saveWithInvalidIDThrowsIllegalArgumentException() {
                PlayerMock player1Mock = server.addPlayer();
                UUID player1UUID = player1Mock.getUniqueId();
                player1Mock.disconnect();

                OfflinePlayer player1 = server.getOfflinePlayer(player1UUID);
                ItemStack item1 = new ItemStack(Material.OAK_PLANKS, 35);

                Pickup pickup1 = new Pickup(-2, player1, item1, 2007, false, 0L);

                // Save trade
                Exception exception = assertThrows(Exception.class, () -> repository.save(pickup1));
                assertEquals("Invalid pickup ID in the pickup passed into SQLite pickup repository", exception.getMessage());
            }
        }

        @Nested
        class TestDelete {
            @Test
            void delete() {
                Exception exception = assertThrows(Exception.class, () -> repository.delete(0));
                assertEquals("DELETE feature not available in the PickupRepository", exception.getMessage());
            }
        }
    }
}
