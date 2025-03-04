package dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite;

import dev.onebiteaidan.worldshop.DataManagement.Repositories.PickupRepository;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Pickup;
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

import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLitePickupSchema.Column.*;
import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLitePickupSchema.PICKUPS_INIT_COMMAND;
import static dev.onebiteaidan.worldshop.DataManagement.Repositories.SQLite.SQLiteSchema.SQLitePickupSchema.PICKUPS_TABLE;

public class SQLitePickupRepository implements PickupRepository {

    Connection database;

    public SQLitePickupRepository(Connection connection) {
        this.database = connection;
        initializeTable();
    }

    private void initializeTable() {
        try {
            PreparedStatement ps = database.prepareStatement(PICKUPS_INIT_COMMAND);
            ps.execute();
        } catch (SQLException e) {
            Logger.severe("Failed to initialize PICKUPS table in the SQLite database!");
            Logger.logStacktrace(e);
        }
    }

    @Override
    public Pickup findById(int pickupID) {
        if (pickupID < 0) {
            throw new IllegalArgumentException("Pickup IDs must be greater than or equal to zero!");
        }

        String cmd = "SELECT * FROM " + PICKUPS_TABLE + " WHERE " + PICKUP_ID + " = ?;";
        try (PreparedStatement ps = database.prepareStatement(cmd)) {
            ps.setInt(1, pickupID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractPickupFromResultSet(rs);
            }
        } catch (SQLException e) {
            Logger.severe("Error occurred when trying to read Pickup with ID '" + pickupID + "' from the SQLite pickup repository.");
            Logger.logStacktrace(e);
        }

        return null;
    }

    @Override
    public List<Pickup> findAll() {
        ArrayList<Pickup> pickups = new ArrayList<>();

        String cmd = "SELECT * FROM " + PICKUPS_TABLE + ";";
        try (PreparedStatement ps = database.prepareStatement(cmd)) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                pickups.add(extractPickupFromResultSet(rs));
            }
        } catch (SQLException e) {
            Logger.severe("Failed to retrieve all pickups from the SQLite pickup repository.");
            Logger.logStacktrace(e);
        }

        return pickups;
    }

    @Override
    public void save(Pickup pickup) {
        // Check if the pickup has an ID
        if (pickup.getPickupID() == -1) {
            // Pickup has no ID, save new pickup
            pickup.setPickupID(getNextPickupID());
        }

        if (pickup.getPickupID() < -1) {
            // Pickup ID is invalid
            throw new IllegalArgumentException("Invalid pickup ID in the pickup passed into SQLite pickup repository");
        }

        String cmd = "INSERT INTO " + PICKUPS_TABLE + " (" + PICKUP_ID + ", " + PLAYER_UUID + ", " + PICKUP_ITEM + ", " + TRADE_ID + ", " + COLLECTED + ", " + COLLECTION_TIMESTAMP + ") " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(" + PICKUP_ID + ") DO UPDATE SET " +
                PLAYER_UUID + "= excluded." + PLAYER_UUID + ", " +
                PICKUP_ITEM + "= excluded." + PICKUP_ITEM + ", " +
                TRADE_ID + "= excluded." + TRADE_ID + ", " +
                COLLECTED + "= excluded." + COLLECTED + ", " +
                COLLECTION_TIMESTAMP + "= excluded." + COLLECTION_TIMESTAMP + ";";

        try (PreparedStatement ps = database.prepareStatement(cmd)) {
            ps.setInt(1, pickup.getPickupID());
            ps.setString(2, pickup.getPlayer().getUniqueId().toString());
            ps.setBytes(3, pickup.getItem().serializeAsBytes());
            ps.setInt(4, pickup.getTradeID());
            ps.setBoolean(5, pickup.isCollected());
            ps.setLong(6, pickup.getCollectionTimestamp());

            ps.executeUpdate();

        } catch (SQLException e) {
            Logger.severe("Filed to save pickup in SQLite Pickup Repository.");
            Logger.logStacktrace(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        throw new NotImplementedException("DELETE feature not available in the PickupRepository");
    }

    private int getNextPickupID() {
        String cmd = "SELECT MAX(" + PICKUP_ID + ") AS max_id FROM " + PICKUPS_TABLE + ";";

        try {
            PreparedStatement ps = database.prepareStatement(cmd);
            ResultSet rs = ps.executeQuery();

            int id = rs.getInt("max_id");

            if (rs.wasNull()) {
                return 0;
            } else {
                return id + 1;
            }

        } catch (SQLException e) {
            Logger.severe("Failed to get next pickup ID from the SQLite database.");
            Logger.logStacktrace(e);
        }

        return -1;
    }

    private Pickup extractPickupFromResultSet(ResultSet rs) throws SQLException {
        int pickupID = rs.getInt(PICKUP_ID.toString());
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString(PLAYER_UUID.toString())));
        ItemStack item = ItemStack.deserializeBytes(rs.getBytes(PICKUP_ITEM.toString()));
        int tradeID = rs.getInt(TRADE_ID.toString());
        boolean collected = rs.getBoolean(COLLECTED.toString());
        long collectionTimestamp = rs.getLong(COLLECTION_TIMESTAMP.toString());

        return new Pickup(
                pickupID,
                player,
                item,
                tradeID,
                collected,
                collectionTimestamp
        );
    }
}
