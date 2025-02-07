package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite;

import dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteTradeRepository;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.TradeStatus;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteTradeSchema.TRADES_TABLE;
import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteTradeSchema.TRADES_INIT_COMMAND;
import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteTradeSchema.Column.*;
import static org.junit.jupiter.api.Assertions.*;

class SQLiteTradeRepositoryTest {

    @Test
    void constructorShouldInitializeEmptyDatabaseWithTradesTable() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            // Initialize the repository
            SQLiteTradeRepository repository = new SQLiteTradeRepository(connection);

            // Verify the TRADES table was created
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TRADES_TABLE + "';")) {

                assertTrue(resultSet.next(), "Table " + TRADES_TABLE + " should be created.");
            }

            // Verify table columns
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + TRADES_TABLE + ");")) {

                Map<String, String> columns = new HashMap<>();
                while (resultSet.next()) {
                    columns.put(resultSet.getString("name"), resultSet.getString("type"));
                }

                assertEquals(TRADE_ID.getColumnType(), columns.get(TRADE_ID.getColumnName()), TRADE_ID + " column type mismatch");
                assertEquals(SELLER_UUID.getColumnType(), columns.get(SELLER_UUID.getColumnName()), SELLER_UUID + " column type mismatch");
                assertEquals(BUYER_UUID.getColumnType(), columns.get(BUYER_UUID.getColumnName()), BUYER_UUID + " column type mismatch");
                assertEquals(ITEM_OFFERED.getColumnType(), columns.get(ITEM_OFFERED.getColumnName()), ITEM_OFFERED + " column type mismatch");
                assertEquals(ITEM_REQUESTED.getColumnType(), columns.get(ITEM_REQUESTED.getColumnName()), ITEM_REQUESTED + " column type mismatch");
                assertEquals(TRADE_STATUS.getColumnType(), columns.get(TRADE_STATUS.getColumnName()), TRADE_STATUS + " column type mismatch");
                assertEquals(LISTING_TIMESTAMP.getColumnType(), columns.get(LISTING_TIMESTAMP.getColumnName()), LISTING_TIMESTAMP + " column type mismatch");
                assertEquals(COMPLETION_TIMESTAMP.getColumnType(), columns.get(COMPLETION_TIMESTAMP.getColumnName()), COMPLETION_TIMESTAMP + " column type mismatch");
            }

        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    @Test
    void constructorShouldSkipInitializationIfTableAlreadyExists() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            // Populate the table with the schema
            PreparedStatement ps = connection.prepareStatement(TRADES_INIT_COMMAND);
            ps.execute();

        } catch (SQLException e) {
            fail("SQLException Occurred During test initialization process (test case issue): " + e.getMessage());
        }

        // Close and reopen with another try with block
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            // Initialize the repository
            SQLiteTradeRepository repository = new SQLiteTradeRepository(connection);

            // Verify the TRADES table was created
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TRADES_TABLE + "';")) {

                assertTrue(resultSet.next(), "Table " + TRADES_TABLE + " should be created.");
            }

            // Verify table columns
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + TRADES_TABLE + ");")) {

                Map<String, String> columns = new HashMap<>();
                while (resultSet.next()) {
                    columns.put(resultSet.getString("name"), resultSet.getString("type"));
                }

                assertEquals(TRADE_ID.getColumnType(), columns.get(TRADE_ID.getColumnName()), TRADE_ID + " column type mismatch");
                assertEquals(SELLER_UUID.getColumnType(), columns.get(SELLER_UUID.getColumnName()), SELLER_UUID + " column type mismatch");
                assertEquals(BUYER_UUID.getColumnType(), columns.get(BUYER_UUID.getColumnName()), BUYER_UUID + " column type mismatch");
                assertEquals(ITEM_OFFERED.getColumnType(), columns.get(ITEM_OFFERED.getColumnName()), ITEM_OFFERED + " column type mismatch");
                assertEquals(ITEM_REQUESTED.getColumnType(), columns.get(ITEM_REQUESTED.getColumnName()), ITEM_REQUESTED + " column type mismatch");
                assertEquals(TRADE_STATUS.getColumnType(), columns.get(TRADE_STATUS.getColumnName()), TRADE_STATUS + " column type mismatch");
                assertEquals(LISTING_TIMESTAMP.getColumnType(), columns.get(LISTING_TIMESTAMP.getColumnName()), LISTING_TIMESTAMP + " column type mismatch");
                assertEquals(COMPLETION_TIMESTAMP.getColumnType(), columns.get(COMPLETION_TIMESTAMP.getColumnName()), COMPLETION_TIMESTAMP + " column type mismatch");
            }

        } catch (SQLException e) {
            fail("SQLException thrown during test: " + e.getMessage());
        }
    }

    @Nested
    class TestWithCorrectDatabaseInitialization {
        ServerMock server;
        SQLiteTradeRepository repository;

        @BeforeEach
        public void setUp() throws SQLException {
            server = MockBukkit.mock();
            repository = new SQLiteTradeRepository(DriverManager.getConnection("jdbc:sqlite::memory:"));
        }

        @AfterEach
        public void tearDown() {
            MockBukkit.unmock();
        }

        @Nested
        class TestFindById {
            @Test
            void findByIdShouldReturnSameTradeAsInserted() {
                PlayerMock buyerMock = server.addPlayer();
                PlayerMock sellerMock = server.addPlayer();

                UUID buyerUUID = buyerMock.getUniqueId();
                UUID sellerUUID = sellerMock.getUniqueId();

                buyerMock.disconnect();
                sellerMock.disconnect();

                OfflinePlayer buyer = server.getOfflinePlayer(buyerUUID);
                OfflinePlayer seller = server.getOfflinePlayer(sellerUUID);
                TradeStatus tradeStatus = TradeStatus.OPEN;
                ItemStack itemOffered = new ItemStack(Material.DIAMOND_AXE);
                ItemStack itemRequested = new ItemStack(Material.OAK_PLANKS, 35);

                Trade trade = new Trade(100, tradeStatus, seller, buyer, itemOffered, itemRequested, 0L, 1L);

                // Save trade to database
                repository.save(trade);

                // Retrieve the trade
                Trade tradeRetrieved = repository.findById(100);

                // Compare
                assertEquals(trade, tradeRetrieved);
            }

            @Test
            void findByIdWithMissingIDShouldReturnNull() {
                // Retrieve the trade with the missing ID 101
                assertNull(repository.findById(101));
            }

            @Test
            void findByIdWithInvalidIDShouldThrowIllegalArgumentException() {
                // Retrieve a trade with the tradeID of -1
                Exception exception = assertThrows(Exception.class, () -> repository.findById(-1));
                assertEquals("Trade IDs must be greater than or equal to zero!", exception.getMessage());
            }
        }

        @Nested
        class TestFindAll {
            @Test
            void findAllShouldReturnAllTrades() {
                PlayerMock buyerMock = server.addPlayer();
                PlayerMock sellerMock = server.addPlayer();

                UUID buyerUUID = buyerMock.getUniqueId();
                UUID sellerUUID = sellerMock.getUniqueId();

                buyerMock.disconnect();
                sellerMock.disconnect();

                OfflinePlayer buyer = server.getOfflinePlayer(buyerUUID);
                OfflinePlayer seller = server.getOfflinePlayer(sellerUUID);
                TradeStatus tradeStatus = TradeStatus.OPEN;
                ItemStack itemOffered = new ItemStack(Material.DIAMOND_AXE);
                ItemStack itemRequested = new ItemStack(Material.OAK_PLANKS, 35);

                Trade trade1 = new Trade(100, tradeStatus, seller, buyer, itemOffered, itemRequested, 0L, 1L);
                Trade trade2 = new Trade(101, tradeStatus, buyer, seller, itemRequested, itemOffered, 1L, 2L);

                repository.save(trade1);
                repository.save(trade2);

                List<Trade> trades = repository.findAll();

                assertEquals(2, trades.size());
                assertEquals(100, trades.get(0).getTradeID());
                assertEquals(101, trades.get(1).getTradeID());

                assertEquals(trades.get(0), trade1);
                assertEquals(trades.get(1), trade2);
            }

            @Test
            void findAllShouldReturnNoTrades() {
                List<Trade> trades = repository.findAll();
                assertTrue(trades.isEmpty());
            }
        }

        @Nested
        class TestSave {

            @Test
            void saveWithIDShouldUpdateObjectInDatabase() {
                // Prepopulate database with trade
                PlayerMock buyerMock = server.addPlayer();
                PlayerMock sellerMock = server.addPlayer();

                UUID buyerUUID = buyerMock.getUniqueId();
                UUID sellerUUID = sellerMock.getUniqueId();

                buyerMock.disconnect();
                sellerMock.disconnect();

                OfflinePlayer buyer = server.getOfflinePlayer(buyerUUID);
                OfflinePlayer seller = server.getOfflinePlayer(sellerUUID);
                TradeStatus tradeStatus = TradeStatus.OPEN;
                ItemStack itemOffered = new ItemStack(Material.DIAMOND_AXE);
                ItemStack itemRequested = new ItemStack(Material.OAK_PLANKS, 35);

                Trade trade = new Trade(100, tradeStatus, seller, buyer, itemOffered, itemRequested, 0L, 1L);

                // Save trade to database
                repository.save(trade);
                assertEquals(1, repository.findAll().size());

                // Retrieve save from database, modify it and save it.
                Trade trade2 = repository.findById(100);

                assertEquals(trade, trade2);

                trade2.setListingTimestamp(10L);
                trade2.setCompletionTimestamp(25L);
                repository.save(trade2);

                // Retrieve again and confirm that it modified the trade.
                Trade trade3 = repository.findById(100);

                assertEquals(trade2, trade3);

                assertEquals(1, repository.findAll().size());
            }

            @Test
            void saveWithoutIDShouldAddObjectToDatabase() {
                // Create trade with no ID
                PlayerMock sellerMock = server.addPlayer();
                UUID sellerUUID = sellerMock.getUniqueId();
                sellerMock.disconnect();

                OfflinePlayer seller = server.getOfflinePlayer(sellerUUID);
                ItemStack itemOffered = new ItemStack(Material.DIAMOND_AXE);
                ItemStack itemRequested = new ItemStack(Material.OAK_PLANKS, 35);

                Trade trade = new Trade((Player) seller, itemOffered, itemRequested);

                // Save trade
                repository.save(trade);

                // Confirm it was given a trade ID
                List<Trade> trades = repository.findAll();

                assertEquals(1, trades.size());
                assertEquals(0,trades.get(0).getTradeID());
            }

            @Test
            void saveWithInvalidIDThrowsIllegalArgumentException() {
                PlayerMock buyerMock = server.addPlayer();
                PlayerMock sellerMock = server.addPlayer();

                UUID buyerUUID = buyerMock.getUniqueId();
                UUID sellerUUID = sellerMock.getUniqueId();

                buyerMock.disconnect();
                sellerMock.disconnect();

                OfflinePlayer buyer = server.getOfflinePlayer(buyerUUID);
                OfflinePlayer seller = server.getOfflinePlayer(sellerUUID);
                TradeStatus tradeStatus = TradeStatus.OPEN;
                ItemStack itemOffered = new ItemStack(Material.DIAMOND_AXE);
                ItemStack itemRequested = new ItemStack(Material.OAK_PLANKS, 35);

                Trade trade = new Trade(-99, tradeStatus, seller, buyer, itemOffered, itemRequested, 0L, 1L);

                // Save trade
                Exception exception = assertThrows(Exception.class, () -> repository.save(trade));
                assertEquals("Invalid trade ID in the trade passed into SQLite trade repository", exception.getMessage());
            }
        }

        @Nested
        class TestDelete {
            @Test
            void delete() {
                Exception exception = assertThrows(Exception.class, () -> repository.delete(0));
                assertEquals("DELETE feature not available in the TradeRepository", exception.getMessage());
            }
        }
    }
}