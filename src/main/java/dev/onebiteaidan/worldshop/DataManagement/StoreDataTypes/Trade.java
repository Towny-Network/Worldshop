package dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes;

import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;
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

    public ItemStack generateDisplayItem() {
        ItemStack item = itemOffered.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Add trade ID to PDC for easy trade identification
            meta.getPersistentDataContainer().set(new NamespacedKey(WorldShop.getInstance(), "trade_id"),
                    PersistentDataType.INTEGER, tradeID);
            item.setItemMeta(meta);

            // Add lore to item
            List<Component> lore = new ArrayList<>();

            lore.add(Component.text("Price:").color(NamedTextColor.GOLD));
            Component itemName;
            if (itemRequested.getItemMeta().hasDisplayName()) {
                itemName = itemRequested.getItemMeta().displayName();
            } else {
                itemName = Component.text(itemRequested.getType().toString()).color(NamedTextColor.GRAY);
            }
            lore.add(Component.text("- x" + itemRequested.getAmount() + " ").color(NamedTextColor.GRAY).append(itemName));

            if (itemOffered.getItemMeta().hasLore()) {
                lore.add(Component.text(""));
                lore.addAll(Objects.requireNonNull(itemOffered.getItemMeta().lore()));
            }

            // Set the item lore
            meta.lore(lore);
        }

        item.setItemMeta(meta);
        return item;
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
                    buyer.equals(trade.buyer) &&
                    itemOffered.equals(trade.itemOffered) &&
                    itemRequested.equals(trade.itemRequested);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeID, tradeStatus, seller, buyer, itemOffered, itemRequested, listingTimestamp, completionTimestamp);
    }
}