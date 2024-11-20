package dev.onebiteaidan.worldshop.Model.DataManagement.Cache;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Profile;
import dev.onebiteaidan.worldshop.Utils.Logger;
import org.bukkit.OfflinePlayer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerCache extends WriteThroughCache<OfflinePlayer, Profile> {

    //todo: Database param names need to be gotten from a common source

    // Constants assume that COLUMNS[0] is the profile's offlinePlayer.
    private final String TABLE = "PLAYERS";
    private final String[] COLUMNS = {
            "PLAYER_UUID",
            "PURCHASES",
            "SALES"
    };

    public PlayerCache(Database database) {
        super(database);
        init();
    }

    protected void init() {
        String command = "SELECT * FROM " + TABLE + ";";
        try (ResultSet rs = database.executeQuery(command, new Object[]{})) {

            while (rs.next()) {
                Profile p = new Profile(rs);
                put(p.getPlayer(), p);
            }
        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }
    }

    /**
     * Generates an UPSERT database command.
     * Will attempt to insert first. On conflicting keys, it will update the matching key (OfflinePlayer UUID).
     * @param key Profile's OfflinePlayer object
     * @param value The profile's Profile object
     * @return Returns an SQL command that is ready to have its inputs sanitized.
     */
    @Override
    protected String generateUpdateCommand(OfflinePlayer key, Profile value) {
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
    protected Object[] generateUpdateParameters(OfflinePlayer key, Profile value) {
        return new Object[]{
                value.getPlayer().getUniqueId(),
                value.getPurchases(),
                value.getSales()
        };
    }

    /**
     * Generates a DELETE command.
     * @param key UUID of Profile's OfflinePlayer to delete
     * @return Returns an SQL command that is ready to have its inputs sanitized.
     */
    @Override
    protected String generateDeleteCommand(OfflinePlayer key) {
        return "DELETE FROM " + TABLE + " WHERE " + COLUMNS[0] + " = ?;";
    }

    /**
     * Generates the DELETE command params.
     * @param key UUID of Profile's OfflinePlayer to delete
     * @return Returns the parameter inputs for be used in the command.
     */
    @Override
    protected Object[] generateDeleteParameters(OfflinePlayer key) {
        return new Object[]{key};
    }
}
