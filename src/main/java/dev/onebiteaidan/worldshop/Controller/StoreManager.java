package dev.onebiteaidan.worldshop.Controller;

import dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.PickupRepository;
import dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLitePickupRepository;
import dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteTradeRepository;
import dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.TradeRepository;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.TradeStatus;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class StoreManager {

    private final TradeRepository tradeRepository;
    private final PickupRepository pickupRepository;

    // List of players with a refreshable screen open.
    private List<Player> activePlayers;

    public StoreManager(TradeRepository tradeRepository, PickupRepository pickupRepository) {
        // Initialize caches using the database.
        this.tradeRepository = tradeRepository;
        this.pickupRepository = pickupRepository;

        // Initialize other variables
        activePlayers = new ArrayList<>();
    }

    //todo: Ensure trade and pickup actions trigger the correct player profile updates (could be used in events?)

    /**
     * Adds new Trade to the database.
     * Expects Trade::getTradeID to be populated.
     * @param t Trade to insert.
     */
    public void createTrade(Trade t) {
        tradeRepository.save(t);
    }

    /**
     * Retrieves a Trade from the database.
     * @param tradeID TradeID of trade to retrieve.
     */
    public Trade getTrade(int tradeID) {
        return tradeRepository.findById(tradeID);
    }

    /**
     * Changes the TradeStatus of the trade to "REMOVED"
     * Generates respective pickup objects.
     * @param tradeID ID of Trade to remove
     */
    public void removeTrade(int tradeID) {
        Trade t = tradeRepository.findById(tradeID);
        t.setTradeStatus(TradeStatus.REMOVED);
        tradeRepository.save(t);

        // Return offered items to the seller in a Pickup
        Pickup p = new Pickup(t.getSeller(), t.getItemOffered(), t.getTradeID());
        pickupRepository.save(p);
    }

    /**
     * Changes the TradeStatus of the trade to "COMPLETE"
     * Generates respective pickup objects.
     * @param trade Trade object to complete.
     * @param player Player who bought the item to complete the trade.
     */
    public void completeTrade(Trade trade, Player player) {
        trade.setTradeStatus(TradeStatus.COMPLETE);
        trade.setBuyer(player);
        tradeRepository.save(trade);

        // Send offered items to buyer
        Pickup p1 = new Pickup(trade.getBuyer(), trade.getItemOffered(), trade.getTradeID());
        pickupRepository.save(p1);

        // Send requested items to seller
        Pickup p2 = new Pickup(trade.getSeller(), trade.getItemRequested(), trade.getTradeID());
        pickupRepository.save(p2);
    }

    /**
     * Changes the TradeStatus of the Trade to "EXPIRED"
     * Generates respective pickup objects.
     * @param tradeID ID of Trade to complete.
     */
    public void expireTrade(int tradeID) {
        Trade t = tradeRepository.findById(tradeID);
        t.setTradeStatus(TradeStatus.EXPIRED);
        tradeRepository.save(t);

        // Return offered items to the seller in a Pickup
        Pickup p = new Pickup(t.getSeller(), t.getItemOffered(), t.getTradeID());
        pickupRepository.save(p);
    }

    /**
     * Creates a Pickup in the database.
     * @param p Pickup to insert.
     */
    public void createPickup(Pickup p) {
        pickupRepository.save(p);
    }

    public Pickup getPickup(int pickupID) {
        return pickupRepository.findById(pickupID);
    }

    /**
     * Marks a pickup as withdraw by setting its withdrawal time.
     * @param pickupID Pickup to set as withdraw.
     */
    public void withdrawPickup(int pickupID) {
        Pickup p = pickupRepository.findById(pickupID);
        p.setWithdrawnTimestamp(System.currentTimeMillis());
        pickupRepository.save(p);
    }

    public List<Trade> getTrades() {
        return new ArrayList<>(tradeRepository.findAll());
    }

    public List<Pickup> getPickups() {
        return new ArrayList<>(pickupRepository.findAll());
    }

    public void addToUpdateList(Player player) {
        activePlayers.add(player);
    }

    public void removeFromUpdateList(Player player) {
        activePlayers.remove(player);
    }
}