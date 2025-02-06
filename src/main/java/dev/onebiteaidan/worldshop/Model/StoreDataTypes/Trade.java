package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Trade {

    int tradeID;                // Set to -1 when unassigned
    TradeStatus tradeStatus;
    OfflinePlayer seller;
    OfflinePlayer buyer;
    ItemStack itemOffered;
    ItemStack itemRequested;
    long listingTimestamp;
    long completionTimestamp;   // Set to -1L when unassigned


    /**
     * Constructor for when a player creates a new trade via the GUI.
     * @param itemOffered  The item the player is offering for trade.
     * @param seller       The player offering the item.
     * @param itemRequested The item the player wants in return.
     */
    public Trade(Player seller, ItemStack itemOffered, ItemStack itemRequested) {
        this.tradeID = -1;
        this.tradeStatus = TradeStatus.OPEN;
        this.seller = seller;
        this.buyer = null;
        this.itemOffered = itemOffered;
        this.itemRequested = itemRequested;
        this.listingTimestamp = System.currentTimeMillis();
        this.completionTimestamp = -1L;
    }

    /**
     * Constructor for recreating the object from a repository.
     * @param tradeID the ID of the trade
     * @param tradeStatus The TradeStatus of the trade
     * @param seller The seller
     * @param buyer The buyer
     * @param itemOffered The item the seller is selling
     * @param itemRequested The item the seller wants in return
     * @param listingTimestamp The time the listing was published
     * @param completionTimestamp The time that the listing is completed.
     */
    public Trade(int tradeID, TradeStatus tradeStatus, OfflinePlayer seller, OfflinePlayer buyer, ItemStack itemOffered, ItemStack itemRequested, long listingTimestamp, long completionTimestamp) {
        this.tradeID = tradeID;
        this.tradeStatus = tradeStatus;
        this.seller = seller;
        this.buyer = buyer;
        this.itemOffered = itemOffered;
        this.itemRequested = itemRequested;
        this.listingTimestamp = listingTimestamp;
        this.completionTimestamp = completionTimestamp;
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
    public boolean equals(Object o) {
        if (o instanceof Trade) {
            Trade trade = (Trade) o;
            return tradeID == trade.tradeID &&
                    listingTimestamp == trade.listingTimestamp &&
                    completionTimestamp == trade.completionTimestamp &&
                    tradeStatus == trade.tradeStatus &&
                    seller.equals(trade.seller) &&
                    buyer.equals(trade.buyer);
//                    buyer.equals(trade.buyer) &&
//                    itemOffered.equals(trade.itemOffered) &&
//                    itemRequested.equals(trade.itemRequested);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeID, tradeStatus, seller, buyer, itemOffered, itemRequested, listingTimestamp, completionTimestamp);
    }
}