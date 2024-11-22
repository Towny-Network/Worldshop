package dev.onebiteaidan.worldshop.Controller;

import dev.onebiteaidan.worldshop.Model.DataManagement.Cache.PickupCache;
import dev.onebiteaidan.worldshop.Model.DataManagement.Cache.PlayerCache;
import dev.onebiteaidan.worldshop.Model.DataManagement.Cache.TradeCache;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Profile;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.OfflinePlayer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StoreManager {

    private final Database db;
    private final TradeCache trades;
    private final PickupCache pickups;
    private final PlayerCache players;

    public StoreManager() {
        // todo: Verify + Init DB Schema

        // Initialize caches using the database.
        this.db = WorldShop.getDatabase();
        this.trades =  new TradeCache(db);
        this.pickups = new PickupCache(db);
        this.players = new PlayerCache(db);

    }

    /**
     * Adds new Trade to the database.
     * Expects Trade::getTradeID to be populated.
     * @param t Trade to insert.
     */
    public void createTrade(Trade t) {
        trades.put(t.getTradeID(), t);
    }

    /**
     * Retrieves a Trade from the database.
     * @param tradeID TradeID of trade to retrieve.
     */
    public Trade getTrade(int tradeID) {
        return trades.get(tradeID);
    }

    /**
     * Changes the TradeStatus of the trade to "REMOVED"
     * Generates respective pickup objects.
     * @param tradeID ID of Trade to remove
     */
    public void removeTrade(int tradeID) {
        Trade t = trades.get(tradeID);
        t.setTradeStatus(TradeStatus.REMOVED);
        trades.put(tradeID, t);

        // Return offered items to the seller in a Pickup
        Pickup p = new Pickup(nextPickupID(), t.getSeller(), t.getItemOffered(), t.getTradeID());
        pickups.put(p.getPickupID(), p);
    }

    /**
     * Changes the TradeStatus of the trade to "COMPLETE"
     * Generates respective pickup objects.
     * @param tradeID ID of Trade to complete.
     */
    public void completeTrade(int tradeID) {
        Trade t = trades.get(tradeID);
        t.setTradeStatus(TradeStatus.COMPLETE);
        trades.put(tradeID, t);

        // Send offered items to buyer
        Pickup p1 = new Pickup(nextPickupID(), t.getBuyer(), t.getItemOffered(), t.getTradeID());
        pickups.put(p1.getPickupID(), p1);

        // Send requested items to seller
        Pickup p2 = new Pickup(nextPickupID(), t.getSeller(), t.getItemRequested(), t.getTradeID());
        pickups.put(p2.getPickupID(), p2);
    }

    /**
     * Changes the TradeStatus of the Trade to "EXPIRED"
     * Generates respective pickup objects.
     * @param tradeID ID of Trade to complete.
     */
    public void expireTrade(int tradeID) {
        Trade t = trades.get(tradeID);
        t.setTradeStatus(TradeStatus.EXPIRED);
        trades.put(tradeID, t);

        // Return offered items to the seller in a Pickup
        Pickup p = new Pickup(nextPickupID(), t.getSeller(), t.getItemOffered(), t.getTradeID());
        pickups.put(p.getPickupID(), p);
    }

    /**
     * Creates a Pickup in the database.
     * @param p Pickup to insert.
     */
    public void createPickup(Pickup p) {
        pickups.put(p.getPickupID(), p);
    }

    public Pickup getPickup(int pickupID) {
        return pickups.get(pickupID);
    }

    /**
     * Marks a pickup as withdraw by setting its withdrawal time.
     * @param pickupID Pickup to set as withdraw.
     */
    public void withdrawPickup(int pickupID) {
        Pickup p = pickups.get(pickupID);
        p.setWithdrawnTimestamp(System.currentTimeMillis());
        pickups.put(pickupID, p);
    }

    /**
     * Creates a new Profile object and stores it in the database.
     * @param p OfflinePlayer object to create a profile for.
     */
    public void createPlayerProfile(OfflinePlayer p) {
        Profile profile = new Profile(p, 0, 0);
        players.put(p, profile);
    }

    /**
     * Retrieves a Profile object from the database.
     * @param p OfflinePlayer object to look up the record of.
     * @return Returns corresponding Profile object.
     */
    public Profile getPlayerProfile(OfflinePlayer p) {
        return players.get(p);
    }

    /**
     * Gets the next trade ID for the database.
     * @return Returns next highest ID. Returns -1 if command fails.
     */
    public int nextTradeID() {
        //todo: Table and Column name need to be pulled from common source.
        String command = "SELECT MAX(TRADE_ID) FROM " + "TRADES" + ";";
        try (ResultSet rs = db.executeQuery(command, new Object[]{})) {
            return rs.getInt("TRADE_ID");
        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }

        return -1;
    }

    /**
     * Gets the next pickup ID for the database.
     * @return Returns next highest ID. Returns -1 if command fails.
     */
    public int nextPickupID() {
        //todo: Table and Column name need to be pulled from common source.
        String command = "SELECT MAX(PICKUP_ID) FROM " + "PICKUPS" + ";";
        try (ResultSet rs = db.executeQuery(command, new Object[]{})) {
            return rs.getInt("PICKUP_ID");
        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }

        return -1;
    }

}