package dev.onebiteaidan.worldshop.StoreDataTypes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;


public class Trade {
    ItemStack forSale;
    ItemStack wanted;
    ItemStack displayItem;
    int amountWanted;

    TradeStatus status;
    Player seller;
    Player buyer;
    int tradeID;
    long timeListed;// Unix time


    /**
     * Constructor for when a player creates a new trade via the gui.
     *
     * @param forSale      Item player is trading away
     * @param wanted       Item player is trading for
     * @param amountWanted Amount they want
     * @param seller       Person selling the forSale item
     */
    public Trade(ItemStack forSale, ItemStack wanted, int amountWanted, Player seller, int tradeID) {
        this.forSale = forSale;
        this.wanted = wanted;
        this.amountWanted = amountWanted;
        this.seller = seller;

        this.status = TradeStatus.OPEN;
        this.buyer = null;
        this.tradeID = tradeID;
        this.timeListed = System.currentTimeMillis();

        ItemStack displayItem = new ItemStack(forSale.getType(), forSale.getAmount());
        ItemMeta displayItemMeta = displayItem.getItemMeta();

        // Items that have displaynames are items that have been renamed to something other than their original title.
        // Therefore, we have to implement this shit system
        if (forSale.getItemMeta().hasDisplayName()) {
            displayItemMeta.setDisplayName(ChatColor.YELLOW + forSale.getItemMeta().getDisplayName() + " x" + this.forSale.getAmount());
        } else {
            String s = forSale.getType().toString();
            String[] parts = s.split("_");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].toLowerCase();
                parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
            }

            displayItemMeta.setDisplayName(ChatColor.YELLOW + String.join(" ", parts) + " x" + this.forSale.getAmount());
        }

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Being Sold By: " + this.seller.getDisplayName());

        if (forSale.getItemMeta().hasLore()) {
            lore.add(ChatColor.DARK_GRAY + "===================");
            lore.add("");
            for (String s : forSale.getItemMeta().getLore()) {
                lore.add(ChatColor.DARK_PURPLE + s);
            }
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "===================");
        }

        lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Click " + ChatColor.RESET + "" + ChatColor.GOLD + "to buy this item");

        displayItemMeta.setLore(lore);
        // Adding in trade ID for indentification later on
        displayItemMeta.setLocalizedName(String.valueOf(tradeID));
        displayItem.setItemMeta(displayItemMeta);
        this.displayItem = displayItem;
    }

    /**
     * Constructor for rebuilding from the Database.
     *
     * @param forSale      Item player is trading away
     * @param wanted       Item player is trading for
     * @param amountWanted Amount they want
     * @param displayItem  The item that goes on display in the shop homepage
     * @param seller       Person selling the forSale item
     * @param buyer        Person who bought the item if trade is complete (null otherwise)
     * @param tradeID      ID of the trade;
     * @param timeListed   Time that the trade was listed (in Unix time)
     */
    public Trade(ItemStack forSale, ItemStack wanted, ItemStack displayItem, int amountWanted, Player seller, Player buyer, int tradeID, long timeListed, TradeStatus status) {
        this.forSale = forSale;
        this.wanted = wanted;
        this.displayItem = displayItem;
        this.amountWanted = amountWanted;
        this.seller = seller;

        this.status = status;
        this.tradeID = tradeID;
        this.buyer = buyer;
        this.timeListed = timeListed;

    }

    public ItemStack getForSale() {
        return forSale;
    }

    public void setForSale(ItemStack forSale) {
        this.forSale = forSale;
    }

    public ItemStack getWanted() {
        return wanted;
    }

    public void setWanted(ItemStack wanted) {
        this.wanted = wanted;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public int getAmountWanted() {
        return amountWanted;
    }

    public void setAmountWanted(int amountWanted) {
        this.amountWanted = amountWanted;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public void setStatus(TradeStatus status) {
        this.status = status;
    }

    public Player getSeller() {
        return seller;
    }

    public void setSeller(Player seller) {
        this.seller = seller;
    }

    public Player getBuyer() {
        return buyer;
    }

    public void setBuyer(Player buyer) {
        this.buyer = buyer;
    }

    public int getTradeID() {
        return tradeID;
    }

    public void setTradeID(int tradeID) {
        this.tradeID = tradeID;
    }

    public long getTimeListed() {
        return timeListed;
    }

    public void setTimeListed(long timeListed) {
        this.timeListed = timeListed;
    }
}
