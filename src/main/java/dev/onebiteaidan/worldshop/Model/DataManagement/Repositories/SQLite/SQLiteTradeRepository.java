package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite;

import dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.TradeRepository;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.Utils.Logger;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteTradeSchema.TRADES_INIT_COMMAND;
import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteTradeSchema.TRADES_TABLE;
import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteTradeSchema.Column.*;

public class SQLiteTradeRepository implements TradeRepository {

    Connection database;

    public SQLiteTradeRepository(Connection connection) {
        this.database = connection;
        initializeTable();
    }

    private void initializeTable() {
        try {
            PreparedStatement ps = database.prepareStatement(TRADES_INIT_COMMAND);
            ps.execute();
        } catch (SQLException e) {
            Logger.severe("Failed to initialize TRADES table in the SQLite database!");
            Logger.logStacktrace(e);
        }
    }

    @Override
    public Trade findById(int tradeID) {
        if (tradeID < 0) {
            throw new IllegalArgumentException("Trade IDs must be greater than or equal to zero!");
        }

        String cmd = "SELECT * FROM " + TRADES_TABLE + " WHERE " + TRADE_ID + " = ?;";
        try (PreparedStatement ps = database.prepareStatement(cmd)) {
            ps.setInt(1, tradeID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractTradeFromResultSet(rs);
            }
        } catch (SQLException e) {
            Logger.severe("Error occurred when trying to read Trade with ID '" + tradeID + "' from the SQLite trade repository.");
            Logger.logStacktrace(e);
        }

        return null;
    }

    @Override
    public List<Trade> findAll() {
        ArrayList<Trade> trades = new ArrayList<>();

        String cmd = "SELECT * FROM " + TRADES_TABLE + ";";
        try (PreparedStatement ps = database.prepareStatement(cmd)) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                trades.add(extractTradeFromResultSet(rs));
            }
        } catch (SQLException e) {
            Logger.severe("Failed to retrieve all trades from the SQLite trade repository.");
            Logger.logStacktrace(e);
        }

        return trades;
    }

    @Override
    public void save(Trade trade) {
        // Check if the trade has an ID
        if (trade.getTradeID() == -1) {
            // Trade has no ID, save new trade
            trade.setTradeID(getNextTradeID());
        }

        if (trade.getTradeID() < -1) {
            // Trade ID is invalid
            throw new IllegalArgumentException("Invalid trade ID in the trade passed into SQLite trade repository");
        }

        String cmd = "INSERT INTO " + TRADES_TABLE + " (" + TRADE_ID + ", " + SELLER_UUID + ", " + BUYER_UUID + ", " + ITEM_OFFERED + ", " + ITEM_REQUESTED + ", " + TRADE_STATUS + ", " + LISTING_TIMESTAMP + ", " + COMPLETION_TIMESTAMP + ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(" + TRADE_ID + ") DO UPDATE SET " +
                SELLER_UUID + "= excluded." + SELLER_UUID + ", " +
                BUYER_UUID  + "= excluded." + BUYER_UUID + ", " +
                ITEM_OFFERED + "= excluded." + ITEM_OFFERED + ", " +
                ITEM_REQUESTED + "= excluded." + ITEM_REQUESTED + ", " +
                TRADE_STATUS + "= excluded." + TRADE_STATUS + ", " +
                LISTING_TIMESTAMP + "= excluded." + LISTING_TIMESTAMP + ", " +
                COMPLETION_TIMESTAMP + "= excluded." + COMPLETION_TIMESTAMP + ";";

        try (PreparedStatement ps = database.prepareStatement(cmd)) {
            ps.setInt(1, trade.getTradeID());
            ps.setString(2, trade.getSeller().getUniqueId().toString());

            if (trade.getBuyer() == null) {
                ps.setString(3, null);
            } else {
                ps.setString(3, trade.getBuyer().getUniqueId().toString());
            }

            ps.setBytes(4, trade.getItemOffered().serializeAsBytes());
            ps.setBytes(5, trade.getItemRequested().serializeAsBytes());

            ps.setInt(6, trade.getTradeStatus().ordinal());
            ps.setLong(7, trade.getListingTimestamp());
            ps.setLong(8, trade.getCompletionTimestamp());

            ps.executeUpdate();

        } catch (SQLException e) {
            Logger.severe("Filed to save trade in SQLite Trade Repository.");
            Logger.logStacktrace(e);
        }
    }

    @Override
    public void delete(int id) {
        throw new NotImplementedException("DELETE feature not available in the TradeRepository");
    }

    private int getNextTradeID() {
        String cmd = "SELECT MAX(" + TRADE_ID + ") AS max_id FROM " + TRADES_TABLE + ";";

        try {
            PreparedStatement ps = database.prepareStatement(cmd);
            ResultSet rs = ps.executeQuery();

            return rs.getInt("max_id");

        } catch (SQLException e) {
            Logger.severe("Failed to get next trade ID from the SQLite database.");
            Logger.logStacktrace(e);
        }

        return -1;
    }

    private Trade extractTradeFromResultSet(ResultSet rs) throws SQLException {
        int tradeID = rs.getInt(TRADE_ID.toString());
        TradeStatus tradeStatus = TradeStatus.values()[rs.getInt(TRADE_STATUS.toString())];
        OfflinePlayer seller = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString(SELLER_UUID.toString())));
        OfflinePlayer buyer = rs.getString(BUYER_UUID.toString()) == null ? null : Bukkit.getOfflinePlayer(UUID.fromString(rs.getString(BUYER_UUID.toString())));
        ItemStack itemOffered = ItemStack.deserializeBytes(rs.getBytes(ITEM_OFFERED.toString()));
        ItemStack itemRequested = ItemStack.deserializeBytes(rs.getBytes(ITEM_REQUESTED.toString()));
        long listingTimestamp = rs.getLong(LISTING_TIMESTAMP.toString());
        long completionTimestamp = rs.getLong(COMPLETION_TIMESTAMP.toString());

        return new Trade(
                tradeID,
                tradeStatus,
                seller,
                buyer,
                itemOffered,
                itemRequested,
                listingTimestamp,
                completionTimestamp
        );
    }
}
