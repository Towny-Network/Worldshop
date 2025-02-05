package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite;

import dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.ProfileRepository;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Profile;
import dev.onebiteaidan.worldshop.Utils.Logger;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteProfileSchema.Column.*;
import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteProfileSchema.PROFILES_INIT_COMMAND;
import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteProfileSchema.PROFILES_TABLE;

public class SQLiteProfileRepository implements ProfileRepository {

    Connection database;

    public SQLiteProfileRepository(Connection connection) {
        this.database = connection;
        initializeTable();
    }

    private void initializeTable() {
        try {
            PreparedStatement ps = database.prepareStatement(PROFILES_INIT_COMMAND);
            ps.execute();
        } catch (SQLException e) {
            Logger.severe("Failed to initialize PLAYERS table in the SQLite database!");
            Logger.logStacktrace(e);
        }
    }

    @Override
    public Profile findById(UUID uuid) {
        String cmd = "SELECT * FROM " + PROFILES_TABLE + " WHERE " + PLAYER_UUID + " = ?;";
        try (PreparedStatement ps = database.prepareStatement(cmd)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractProfileFromResultSet(rs);
            }
        } catch (SQLException e) {
            Logger.severe("Error occurred when trying to read Profile with player UUID '" + uuid + "' from the SQLite profile repository.");
            Logger.logStacktrace(e);
        }

        return null;
    }

    @Override
    public List<Profile> findAll() {
        ArrayList<Profile> profiles = new ArrayList<>();

        String cmd = "SELECT * FROM " + PROFILES_TABLE + ";";
        try (PreparedStatement ps = database.prepareStatement(cmd)) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                profiles.add(extractProfileFromResultSet(rs));
            }
        } catch (SQLException e) {
            Logger.severe("Failed to retrieve all profiles from the SQLite profile repository.");
            Logger.logStacktrace(e);
        }

        return profiles;
    }

    @Override
    public void save(Profile profile) {
        String cmd = "INSERT INTO " + PROFILES_TABLE + " (" + PLAYER_UUID + ", " + PURCHASES + ", " + SALES + ") " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT(" + PLAYER_UUID + ") DO UPDATE SET " +
                PURCHASES + "= excluded." + PURCHASES + ", " +
                SALES + "= excluded." + SALES + ";";

        try (PreparedStatement ps = database.prepareStatement(cmd)) {
            ps.setString(1, profile.getPlayer().getUniqueId().toString());
            ps.setInt(2, profile.getPurchases());
            ps.setInt(3, profile.getPurchases());

            ps.executeUpdate();

        } catch (SQLException e) {
            Logger.severe("Filed to save profile in SQLite Profile Repository.");
            Logger.logStacktrace(e);
        }
    }

    @Override
    public void delete(UUID uuid) {
        throw new NotImplementedException("DELETE feature not available in the ProfileRepository");
    }

    private Profile extractProfileFromResultSet(ResultSet rs) throws SQLException {
        UUID playerUUID = UUID.fromString(rs.getString(PLAYER_UUID.toString()));
        int purchases = rs.getInt(PURCHASES.toString());
        int sales = rs.getInt(SALES.toString());

        return new Profile(
                Bukkit.getOfflinePlayer(playerUUID),
                purchases,
                sales
        );
    }
}
