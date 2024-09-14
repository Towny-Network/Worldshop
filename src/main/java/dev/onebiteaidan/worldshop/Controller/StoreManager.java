package dev.onebiteaidan.worldshop.Controller;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database;
import dev.onebiteaidan.worldshop.Model.DataManagement.QueryBuilder;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.View.Screen;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.*;

public class StoreManager {

    private static StoreManager instance;

    private final ArrayList<Player> playerUpdateList;


    private StoreManager() {
        playerUpdateList = new ArrayList<>();
    }

    public static StoreManager getInstance() {
        if (instance == null) {
            instance = new StoreManager();
        }

        return instance;
    }


    public void createTrade(Trade t) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try {
            qb.insertInto("trades", "seller_uuid, buyer_uuid, for_sale, in_return, status, time_listed, time_completed")
                    .values("?,?,?,?,?,?,?")
                    .addParameter(t.getSeller().getUniqueId().toString())
                    .addParameter(null)
                    .addParameter(t.getForSale())
                    .addParameter(t.getInReturn())
                    .addParameter(t.getStatus().ordinal())
                    .addParameter(t.getTimeListed())
                    .addParameter(0L)
                    .executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void completeTrade(Player buyer, int tradeID) {
        Trade t = getTradeFromTradeID(tradeID);
        t.setStatus(TradeStatus.COMPLETE);
        t.setBuyer(buyer);
        t.setTimeCompleted(System.currentTimeMillis());

        Pickup buyerPickup = new Pickup(buyer, t.getForSale(), tradeID, false, 0L);
        Pickup sellerPickup = new Pickup(t.getSeller(), t.getInReturn(), tradeID, false, 0L);

        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try {
            qb.insertInto("pickups", "player_uuid, pickup_item, trade_id, collected, time_collected")
                    .values("?,?,?,?,?")
                    .addParameter(buyerPickup.getPlayer().getUniqueId().toString())
                    .addParameter(buyerPickup.getItem())
                    .addParameter(buyerPickup.getTradeID())
                    .addParameter(buyerPickup.isWithdrawn())
                    .addParameter(buyerPickup.getTimeWithdrawn())
                    .executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            qb.insertInto("pickups", "player_uuid, pickup_item, trade_id, collected, time_collected")
                    .values("?,?,?,?,?")
                    .addParameter(sellerPickup.getPlayer().getUniqueId().toString())
                    .addParameter(sellerPickup.getItem())
                    .addParameter(sellerPickup.getTradeID())
                    .addParameter(sellerPickup.isWithdrawn())
                    .addParameter(sellerPickup.getTimeWithdrawn())
                    .executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Todo: Add something here to update all players with the store currently open.
        // Also a one size fits all gui creator would be really sweet.
        updateAllPlayers(buyer);
    }

    public void pickupCompletedTrade(Player collector, Trade trade) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try {
            qb.update("pickups")
                    .set("collected = ?, time_collected = ?")
                    .where("trade_id = ? AND player_uuid = ?")
                    .addParameter(true)
                    .addParameter(System.currentTimeMillis())
                    .addParameter(trade.getTradeID())
                    .addParameter(collector.getUniqueId().toString())
                    .executeUpdate();

        } catch (SQLException exception) {
            Utils.logStacktrace(exception);
        }


        collector.getInventory().addItem(trade.get


        p.getInventory().addItem(e.getCurrentItem());
        e.getInventory().removeItem(e.getCurrentItem());
    }

    public void pickupCompletedTrade(Player collector, int tradeID) {
        pickupCompletedTrade(collector, getTradeFromTradeID(tradeID));
    }

    public void addToUpdateList(Player p) {
        this.playerUpdateList.add(p);
    }

    public void removeFromUpdateList(Player p) {
        this.playerUpdateList.remove(p);
    }

    /**
     * Update the store pages of all players to prevent accidental duplication
     * @param ignorePlayer The player who just completed a trade and doesn't need to have their stuff updated
     */
    public void updateAllPlayers(Player ignorePlayer) {
        for (Player player : playerUpdateList) {
            if (player.equals(ignorePlayer)) {
                continue;
            }

            InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
            if (holder instanceof Screen) {
                ((Screen) holder).update();
            }
        }
    }

    public void expireTrade(int tradeID) {
        Trade t = getTradeFromTradeID(tradeID);
        t.setStatus(TradeStatus.EXPIRED);
        t.setTimeCompleted(System.currentTimeMillis());

        Pickup forSalePickup = new Pickup(t.getSeller(), t.getForSale(), tradeID, false, 0L);

        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try {
            qb.insertInto("pickups", "player_uuid, pickup_item, trade_id, collected, time_collected")
                    .values("?,?,?,?,?")
                    .addParameter(forSalePickup.getPlayer().getUniqueId().toString())
                    .addParameter(forSalePickup.getItem())
                    .addParameter(forSalePickup.getTradeID())
                    .addParameter(forSalePickup.isWithdrawn())
                    .addParameter(forSalePickup.getTimeWithdrawn())
                    .executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTrade(int tradeID) {
        Trade t = getTradeFromTradeID(tradeID);
        t.setStatus(TradeStatus.REMOVED);
        t.setTimeCompleted(System.currentTimeMillis());

        Pickup forSalePickup = new Pickup(t.getSeller(), t.getForSale(), tradeID, false, 0L);

        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try {
            qb.insertInto("pickups", "player_uuid, pickup_item, trade_id, collected, time_collected")
                    .values("?,?,?,?,?")
                    .addParameter(forSalePickup.getPlayer().getUniqueId().toString())
                    .addParameter(forSalePickup.getItem())
                    .addParameter(forSalePickup.getTradeID())
                    .addParameter(forSalePickup.isWithdrawn())
                    .addParameter(forSalePickup.getTimeWithdrawn())
                    .executeUpdate();

        } catch (SQLException exception) {
            Utils.logStacktrace(exception);
        }
    }


    /**
     * Gets the trade from the passed in display item.
     * @param displayItem item to find trade of.
     * @return returns the itemstack that has the same trade. Returns NULL of no matching itemstack is found
     */
    public Trade getTradeFromDisplayItem(ItemStack displayItem) {
        if (displayItem.getItemMeta().hasLocalizedName()) {
            Database db = WorldShop.getDatabase();
            QueryBuilder qb = new QueryBuilder(db);

            return getTradeFromTradeID(Integer.parseInt(displayItem.getItemMeta().getLocalizedName()));
        }
        return null;
    }

    /**
     * Get the trade from the associated integer trade ID
     * @param id integer ID associated with the trade.
     * @return returns the trade associated w/ the ID. Returns null of no trade is found
     */
    public Trade getTradeFromTradeID(int id) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try (ResultSet rs = qb
                .select("*")
                .from("trades")
                .where("trade_id = ?")
                .addParameter(id)
                .executeQuery()) {

            rs.next();

            // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
            OfflinePlayer buyer = null;
            String buyerUUID = rs.getString("buyer_uuid");
            if (!rs.wasNull()) {
                buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUUID));
            }

            Trade trade = new Trade(rs.getInt("trade_id"),
                    TradeStatus.values()[rs.getInt("status")],
                    Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller_uuid"))),
                    buyer,
                    ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                    ItemStack.deserializeBytes(rs.getBytes("in_return")),
                    rs.getLong("time_listed"),
                    rs.getLong("time_completed")
            );

            return trade;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<ItemStack> getAllCurrentTradesDisplayItems(Player player) {
        List<ItemStack> items = new ArrayList<>();
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try (ResultSet rs = qb
                    .select("*")
                    .from("trades")
                    .where("status = ? AND seller_uuid = ?")
                    .addParameter(TradeStatus.OPEN.ordinal())
                    .addParameter(player.getUniqueId().toString())
                    .executeQuery()) {

            while (rs.next()) {
                // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
                OfflinePlayer buyer = null;
                String buyerUUID = rs.getString("buyer_uuid");
                if (!rs.wasNull()) {
                    buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUUID));
                }

                items.add(new Trade(rs.getInt("trade_id"),
                        TradeStatus.values()[rs.getInt("status")],
                        Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller_uuid"))),
                        buyer,
                        ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                        ItemStack.deserializeBytes(rs.getBytes("in_return")),
                        rs.getLong("time_listed"),
                        rs.getLong("time_completed")
                ).generateCurrentTradeDisplayItem());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<ItemStack> getAllCompletedTradesItems(Player player) {
        ArrayList<ItemStack> pickups = new ArrayList<>();
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try (ResultSet rs = qb
                    .select("*")
                    .from("pickups")
                    .where("player_uuid = ? AND collected = ?")
                    .addParameter(player.getUniqueId().toString())
                    .addParameter(false)
                    .executeQuery()) {

            while (rs.next()) {

                Pickup p = new Pickup(Bukkit.getPlayer(UUID.fromString(rs.getString("player_uuid"))),
                        ItemStack.deserializeBytes(rs.getBytes("pickup_item")),
                        rs.getInt("trade_id"), rs.getBoolean("collected"),
                        rs.getLong("time_collected")
                );

                ItemStack item = p.getItem();
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setLocalizedName(String.valueOf(p.getTradeID()));
                item.setItemMeta(itemMeta);

                pickups.add(item);
            }

        } catch (SQLException e) {
            Utils.logStacktrace(e);
        }

        return pickups;
    }

    // endregion
}