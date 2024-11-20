package dev.onebiteaidan.worldshop.Model.DataManagement.Cache;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Utils.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TradeCache extends WriteThroughCache<Integer, Trade> {

    //todo: Database param names need to be gotten from a common source

    // Constants assume that COLUMNS[0] is the trade's id.
    private final String TABLE = "TRADES";
    private final String[] COLUMNS = {
            "TRADE_ID",
            "SELLER_UUID",
            "BUYER_UUID",
            "ITEM_OFFERED",
            "ITEM_REQUESTED",
            "TRADE_STATUS",
            "LISTING_TIMESTAMP",
            "COMPLETION_TIMESTAMP"
    };

    public TradeCache(Database database) {
        super(database);
        init();
    }

    /**
     * Populates the data structure with information from the database.
     */
    protected void init() {
        String command = "SELECT * FROM " + TABLE + ";";
        try (ResultSet rs = database.executeQuery(command, new Object[]{})) {

            while (rs.next()) {
                Trade t = new Trade(rs);
                put(t.getTradeID(), t);
            }
        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }
    }

    /**
     * Generates an UPSERT database command.
     * Will attempt to insert first. On conflicting keys, it will update the matching key (Trade ID).
     * @param key Trade's TradeID
     * @param value The Trade object to pass in.
     * @return Returns an SQL command that is ready to have its inputs sanitized.
     */
    @Override
    protected String generateUpdateCommand(Integer key, Trade value) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(TABLE);
        sb.append(" (");

        // Add in all column names
        for (int i = 0; i < COLUMNS.length; i++) {
            sb.append(COLUMNS[i]);

            // If not last column name, add a comma.
            if (i != COLUMNS.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(") VALUES (");

        // Add in placeholder column values
        for (int i = 0; i < COLUMNS.length; i++) {
            sb.append("?");

            // If not last one, add a comma.
            if (i != COLUMNS.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(") ON CONFLICT (");
        sb.append(COLUMNS[0]); // Should be the Trade ID column
        sb.append(") DO UPDATE SET ");

        // Update all values except trade ID
        for (int i = 1;  i < COLUMNS.length; i++) {
            sb.append(COLUMNS[i]).append(" = ?");

            // If not last one, add a comma.
            if (i != COLUMNS.length - 1) {
                sb.append(", ");
            }
        }

        // Finish with a semicolon.
        sb.append(";");

        return sb.toString();
    }

    /**
     * Generates the UPSERT command params.
     * @param key Trade's TradeID
     * @param value The trade object to pass in.
     * @return Returns the parameter inputs to be used in the command.
     */
    @Override
    protected Object[] generateUpdateParameters(Integer key, Trade value) {
        return new Object[]{
                value.getTradeID(),
                value.getTradeStatus(),
                value.getSeller(),
                value.getBuyer(),
                value.getItemOffered(),
                value.getItemRequested(),
                value.getListingTimestamp(),
                value.getCompletionTimestamp()
        };
    }

    /**
     * Generates a DELETE command.
     * @param key TradeID of the trade to  delete.
     * @return Returns an SQL command that is ready to have its inputs sanitized.
     */
    @Override
    protected String generateDeleteCommand(Integer key) {
        return "DELETE FROM " + TABLE + " WHERE " + COLUMNS[0] + " = ?;";
    }

    /**
     * Generates the DELETE command params.
     * @param key Trade's TradeID
     * @return Returns the parameter inputs for be used in the command.
     */
    @Override
    protected Object[] generateDeleteParameters(Integer key) {
        return new Object[]{key};
    }
}