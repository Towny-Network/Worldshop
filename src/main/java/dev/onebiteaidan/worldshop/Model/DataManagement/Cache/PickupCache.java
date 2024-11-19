package dev.onebiteaidan.worldshop.Model.DataManagement.Cache;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;

public class PickupCache extends WriteThroughCache<Integer, Pickup> {
    // Constants assume that COLUMNS[0] is the Pickup's ID.
    private final String TABLE = "PICKUPS";
    private final String[] COLUMNS = {
            "PICKUP_ID",
            "PLAYER_UUID",
            "PICKUP_ITEM",
            "TRADE_ID",
            "COLLECTED",
            "TIME_COLLECTED"
    };

    public PickupCache(Database database) {
        super(database);
    }

    /**
     * Generates an UPSERT database command.
     * Will attempt to insert first. On conflicting keys, it will update the matching key (Pickup ID).
     * @param key Pickup ID
     * @param value The Pickup object.
     * @return Returns an SQL command that is ready to have its inputs sanitized.
     */
    @Override
    protected String generateUpdateCommand(Integer key, Pickup value) {
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
     * @param key Pickup's PickupID
     * @param value The Pickup object to pass in.
     * @return Returns the parameter inputs to be used in the command.
     */
    @Override
    protected Object[] generateUpdateParameters(Integer key, Pickup value) {
        return new Object[]{
                value.getPickupID(),
                value.getPlayer(),
                value.getItem(),
                value.isWithdrawn(),
                value.getTimeWithdrawn()
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
     * @param key Pickup's PickupID
     * @return Returns the parameter inputs for be used in the command.
     */
    @Override
    protected Object[] generateDeleteParameters(Integer key) {
        return new Object[]{key};
    }
}