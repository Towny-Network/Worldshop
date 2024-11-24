package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class Trade {

    int tradeID;
    TradeStatus tradeStatus;
    OfflinePlayer seller;
    OfflinePlayer buyer;
    ItemStack itemOffered;
    ItemStack itemRequested;
    long listingTimestamp;
    long completionTimestamp;


    /**
     * Constructor for when a player creates a new trade via the GUI.
     * @param itemOffered  The item the player is offering for trade.
     * @param seller       The player offering the item.
     * @param itemRequested The item the player wants in return.
     */
    public Trade(Player seller, ItemStack itemOffered, ItemStack itemRequested) {
        this.tradeID = WorldShop.getStoreManager().nextTradeID();
        this.tradeStatus = TradeStatus.OPEN;
        this.seller = seller;
        this.buyer = null;
        this.itemOffered = itemOffered;
        this.itemRequested = itemRequested;
        this.listingTimestamp = System.currentTimeMillis();
        this.completionTimestamp = 0L;
    }

    /**
     * Build a Trade object from an SQL ResultSet
     * Needs to be called within a try-with-resources
     * @param rs ResultSet to build from.
     */
    public Trade(ResultSet rs) throws SQLException {
        this.tradeID = rs.getInt("TRADE_ID");
        this.seller = Bukkit.getPlayer(UUID.fromString(rs.getString("SELLER_UUID")));

        String buyerUUID = rs.getString("BUYER_UUID");
        if (buyerUUID != null) {
            this.buyer = Bukkit.getPlayer(UUID.fromString(buyerUUID));
        } else {
            this.buyer = null;
        }

//        try {
            this.itemOffered = Utils.loadItemStack(rs.getBytes("ITEM_OFFERED"));
            this.itemRequested = Utils.loadItemStack(rs.getBytes("ITEM_REQUESTED"));
//        } catch (IOException e) {
//            Logger.logStacktrace(e);
//        }

        this.tradeStatus = TradeStatus.values()[rs.getInt("TRADE_STATUS")];
        this.listingTimestamp = rs.getLong("LISTING_TIMESTAMP");
        this.completionTimestamp = rs.getLong("COMPLETION_TIMESTAMP");
    }

    public int getTradeID() {
        return tradeID;
    }

    public void setTradeID(int tradeID) {
        this.tradeID = tradeID;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public OfflinePlayer getSeller() {
        return seller;
    }

    public void setSeller(OfflinePlayer seller) {
        this.seller = seller;
    }

    public OfflinePlayer getBuyer() {
        return buyer;
    }

    public void setBuyer(OfflinePlayer buyer) {
        this.buyer = buyer;
    }

    public ItemStack getItemOffered() {
        return itemOffered;
    }

    public void setItemOffered(ItemStack itemOffered) {
        this.itemOffered = itemOffered;
    }

    public ItemStack getItemRequested() {
        return itemRequested;
    }

    public void setItemRequested(ItemStack itemRequested) {
        this.itemRequested = itemRequested;
    }

    public long getListingTimestamp() {
        return listingTimestamp;
    }

    public void setListingTimestamp(long listingTimestamp) {
        this.listingTimestamp = listingTimestamp;
    }

    public long getCompletionTimestamp() {
        return completionTimestamp;
    }

    public void setCompletionTimestamp(long completionTimestamp) {
        this.completionTimestamp = completionTimestamp;
    }

    public DisplayItem generateDisplayItem() {
        ItemStack item = itemOffered.clone();
        DisplayItem displayItem = new DisplayItem(item);
        displayItem.setTradeID(tradeID);
        return displayItem;
    }

    @Override
    public String toString() {
        return "Trade {" +
                "tradeId=" + tradeID +
                ", tradeStatus=" + tradeStatus +
                ", seller=" + (seller != null ? seller.getUniqueId() : "None") +
                ", buyer=" + (buyer != null ? buyer.getUniqueId() : "None") +
                ", itemOffered=" + (itemOffered != null ? itemOffered.getType() : "None") +
                ", itemRequested=" + (itemRequested != null ? itemRequested.getType() : "None") +
                ", listingTimestamp=" + listingTimestamp +
                ", completionTimestamp=" + (completionTimestamp > 0 ? completionTimestamp : "Not completed") +
                '}';
    }
}