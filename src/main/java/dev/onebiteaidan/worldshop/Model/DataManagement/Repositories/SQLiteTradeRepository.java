package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.Utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLiteTradeRepository extends TradeRepository<Integer, Trade> {

    public SQLiteTradeRepository(File filePath) {
        super(filePath);
    }

    @Override
    protected void initializeTable() {
        String cmd = "CREATE TABLE IF NOT EXISTS TRADES" +
                "(" +
                "TRADE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "SELLER_UUID varchar(36)," +
                "BUYER_UUID varchar(36)," +
                "ITEM_OFFERED BLOB," +
                "ITEM_REQUESTED BLOB," +
                "TRADE_STATUS int," +
                "LISTING_TIMESTAMP BIGINT," +
                "COMPLETION_TIMESTAMP BIGINT" +
                ");";
        try {
            PreparedStatement ps = database.prepareStatement(cmd);
        } catch (SQLException e) {
            Logger.severe("Failed to initialize TRADES table in the SQLite database!");
            Logger.logStacktrace(e);
        }
    }

    @Override
    public void save(Integer tradeID, Trade trade) {
        String cmd = "INSERT INTO TRADES (TRADE_ID, SELLER_UUID, BUYER_UUID, ITEM_OFFERED, ITEM_REQUESTED, TRADE_STATUS, LISTING_TIMESTAMP, COMPLETION_TIMESTAMP) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(TRADE_ID) DO UPDATE SET " +
                "SELLER_UUID = excluded.SELLER_UUID, " +
                "BUYER_UUID = excluded.BUYER_UUID, " +
                "ITEM_OFFERED = excluded.ITEM_OFFERED, " +
                "ITEM_REQUESTED = excluded.ITEM_REQUESTED, " +
                "TRADE_STATUS = excluded.TRADE_STATUS, " +
                "LISTING_TIMESTAMP = excluded.LISTING_TIMESTAMP, " +
                "COMPLETION_TIMESTAMP = excluded.COMPLETION_TIMESTAMP;";

        try (PreparedStatement ps = database.prepareStatement(cmd)) {
            ps.setInt(1, tradeID);
            ps.setString(2, trade.getSeller().getUniqueId().toString());
            ps.setString(3, trade.getBuyer().getUniqueId().toString());
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
    public Trade find(Integer tradeID) {
        String cmd = "SELECT * FROM TRADES WHERE TRADE_ID = ?;";
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

        String cmd = "SELECT * FROM TRADES;";
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

    public int getNextTradeID() {
        String cmd = "SELECT MAX(TRADE_ID) AS max_id FROM TRADES;";

        try {
            PreparedStatement ps = database.prepareStatement(cmd);
            ResultSet rs = ps.executeQuery();

        } catch (SQLException e) {
            Logger.severe("Failed to get next trade ID from the SQLite database.");
            Logger.logStacktrace(e);
        }

        return -1;
    }

    private Trade extractTradeFromResultSet(ResultSet rs) throws SQLException {
        return new Trade(
                rs.getInt("TRADE_ID"),
                TradeStatus.values()[rs.getInt("TRADE_STATUS")],
                Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("SELLER_UUID"))),
                Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("BUYER_UUID"))),
                ItemStack.deserializeBytes(rs.getBytes("ITEM_OFFERED")),
                ItemStack.deserializeBytes(rs.getBytes("ITEM_REQUESTED")),
                rs.getLong("LISTING_TIMESTAMP"),
                rs.getLong("COMPLETION_TIMESTAMP")
        );
    }
}
