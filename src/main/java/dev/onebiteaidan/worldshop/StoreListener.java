package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.Utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class StoreListener implements Listener {

    /**
     * Event handler for the main shop screen
     * @param e event
     */
    @EventHandler
    public void onWorldShopScreenClick(InventoryClickEvent e) {
        if (e.getInventory().getSize() == 54 &&
                e.getCurrentItem() != null &&
                e.getView().getItem(49) != null &&
                e.getView().getItem(49).getItemMeta().hasLocalizedName() &&
                e.getView().getItem(49).getItemMeta().getLocalizedName().equals("WorldShopHomeScreen")) {

            e.setCancelled(true);

            int currentPage = Integer.parseInt(e.getInventory().getItem(45).getItemMeta().getLocalizedName());

            switch (e.getRawSlot()) {
                case 45: // Previous Page
                    if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                        WorldShop.getStoreManager().prevPage((Player) e.getWhoClicked(), currentPage);
                    }
                    break;

                case 47: // Search
                    break;

                case 48: // Filter
                    break;

                case 49: // Stats Head Display
                    break;

                case 50: // Sell Item
                    WorldShop.getStoreManager().sellItem((Player) e.getWhoClicked());
                    break;

                case 51: // View Trades
                    WorldShop.getStoreManager().manageTrades((Player) e.getWhoClicked());
                    break;

                case 53: // Next Page
                    if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                        WorldShop.getStoreManager().nextPage((Player) e.getWhoClicked(), currentPage);
                    }
                    break;

                default: // Open buy screen for the item that was clicked on

                    // Make sure the player isn't clicking an item in their hotbar or inventory
                    if (e.getRawSlot() >= 54 && e.getRawSlot() <= 89) {
                        break;
                    }

                    // Open the buy screen
                    WorldShop.getStoreManager().buyItem((Player) e.getWhoClicked(), e.getCurrentItem());
            }
        }
    }

    /**
     * Event handler for the screen where the player sells their items
     * @param e event
     */
    @EventHandler
    public void onSellScreenClick(InventoryClickEvent e) { //FIXME: needs localized name
        if (e.getInventory().getSize() == 27 &&
                e.getView().getItem(18) != null &&
                e.getView().getItem(18).getItemMeta().hasDisplayName() &&
                e.getView().getItem(18).getItemMeta().getLocalizedName().equals("SellItemScreen") &&
                e.getCurrentItem() != null) {

            
            e.setCancelled(true);

            switch(e.getRawSlot()) {
                case 0: // Submit button
                    // Check what the condition of the slot is
                    String name = e.getInventory().getItem(0).getItemMeta().getDisplayName();
                    name = ChatColor.stripColor(name);
                    switch(name) {
                        case "You cannot confirm until you have put in a sell item and a price item!":
                            break;

                        case "Click to Confirm!":
                            // Set the confirm button to Green Check
                            ItemStack fullConfirm = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=");
                            ItemMeta fullConfirmMeta = fullConfirm.getItemMeta();
                            fullConfirmMeta.setDisplayName("Are you sure?");
                            fullConfirm.setItemMeta(fullConfirmMeta);
                            e.getInventory().setItem(0, fullConfirm);
                            return;

                        case "Are you sure?":
                            Inventory inven = e.getInventory();
                            ItemStack forSale = inven.getItem(12);
                            ItemStack inReturn = inven.getItem(15);

                            // Remove first occurrence of a repeat itemstack in the players inventory
                            if (e.getWhoClicked().getInventory().contains(forSale)) {
                                e.getWhoClicked().getInventory().setItem(e.getWhoClicked().getInventory().first(forSale), null);
                            } else {
                                e.getWhoClicked().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Something went wrong. Please open a ticket on the Discord. ERROR CODE: WS0002");
                                WorldShop.getPlugin(WorldShop.class).getLogger().severe("Player attempted to sell an item without it being in their inventory");
                                break;
                            }

                            WorldShop.getStoreManager().createTrade(new Trade((Player) e.getWhoClicked(), forSale, inReturn));

                            // Brings the player back to the main page of the store.
                            WorldShop.getStoreManager().openShop((Player) e.getWhoClicked(), 1);
                            break;
                    }

                    break;


                case 18: // Back button
                    // Brings the player back to the main page of the store.
                    WorldShop.getStoreManager().openShop((Player) e.getWhoClicked(), 1);
                    break;


                case 14: // Increase Price

                    if (!e.getInventory().getItem(15).equals(Material.RED_STAINED_GLASS_PANE) && e.getInventory().getItem(15).getAmount() != 64) {
                        e.getInventory().getItem(15).setAmount(e.getInventory().getItem(15).getAmount() + 1);
                    }
                    break;


                case 16: // Decrease Price

                    if (!e.getInventory().getItem(15).equals(Material.RED_STAINED_GLASS_PANE) && e.getInventory().getItem(15).getAmount() != 1) {
                        e.getInventory().getItem(15).setAmount(e.getInventory().getItem(15).getAmount() - 1);
                    }
                    break;


                case 12: // Reset/remove buy item
                    // Changes current itemstack back to the original
                    // Item player wants to sell
                    ItemStack blankItemSpotButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    ItemMeta blankItemSpotButtonMeta = blankItemSpotButton.getItemMeta();
                    blankItemSpotButtonMeta.setDisplayName("Left click the item in your inventory you want to sell!");
                    blankItemSpotButton.setItemMeta(blankItemSpotButtonMeta);
                    e.getInventory().setItem(12, blankItemSpotButton);
                    break;


                case 15: // Reset/remove price item
                    // Item player wants to receive in trade
                    ItemStack priceButton = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                    ItemMeta priceButtonMeta = priceButton.getItemMeta();
                    priceButtonMeta.setDisplayName("Right Click the item in your inventory you want to receive in trade!");
                    priceButton.setItemMeta(priceButtonMeta);
                    e.getInventory().setItem(15, priceButton);
                    break;


                default:
                    // Check if item exists here and also check if it was a left or right click
                    if (!e.getCurrentItem().getType().equals(Material.AIR) && !e.getCurrentItem().getItemMeta().hasLocalizedName() && !e.getCurrentItem().getItemMeta().getLocalizedName().equals("Divider")) {

                        // Determine if it's a right or left click
                        if (e.getClick().isLeftClick()) { // Set the item we are selling
                            e.getInventory().setItem(12, e.getCurrentItem());

                        } else if (e.getClick().isRightClick()) {
                            ItemStack curr = new ItemStack(e.getCurrentItem());
                            curr.setAmount(1);
                            e.getInventory().setItem(15, curr);
                        }
                    }

                    break;
            }

            // Check if sell and price slots are filled
            if (!e.getInventory().getItem(12).getType().equals(Material.RED_STAINED_GLASS_PANE) &&
                    !e.getInventory().getItem(12).getItemMeta().getDisplayName().equals("Left click the item in your inventory you want to sell!") &&
                    !e.getInventory().getItem(15).getType().equals(Material.RED_STAINED_GLASS_PANE) &&
                    !e.getInventory().getItem(15).getItemMeta().getDisplayName().equals("Left click the item in your inventory you want to sell!")) {

                // Change confirm button to Yellow Check
                ItemStack halfConfirm = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmNDI1YjRkYjdkNjJiMjAwZTg5YzAxM2U0MjFhOWUxMTBiZmIyN2YyZDhiOWY1ODg0ZDEwMTA0ZDAwZjRmNCJ9fX0=");
                ItemMeta halfConfirmMeta = halfConfirm.getItemMeta();
                halfConfirmMeta.setDisplayName("Click to Confirm!");
                halfConfirm.setItemMeta(halfConfirmMeta);
                e.getInventory().setItem(0, halfConfirm);

            }
        }
    }

    /**
     * Event handler for when the buy screen for an item is opened.
     * This updater runs to set the initial status of the checkbox based on if the player has enough of the price item
     * @param e event
     */
    @EventHandler
    public void onOpenBuyScreen(InventoryOpenEvent e) {
        if (e.getView().getItem(0) != null &&
                e.getView().getItem(0).hasItemMeta() &&
                e.getView().getItem(0).getItemMeta().hasLocalizedName() &&
                e.getView().getItem(0).getItemMeta().getLocalizedName().equals("BuyItemScreen")) {

            // Check if player has the required items to buy the item
            if (Utils.getNumOfItems((Player) e.getPlayer(), e.getInventory().getItem(6)) >= e.getInventory().getItem(6).getAmount()) {

                // Change confirm button to Yellow Check
                ItemStack halfConfirm = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmNDI1YjRkYjdkNjJiMjAwZTg5YzAxM2U0MjFhOWUxMTBiZmIyN2YyZDhiOWY1ODg0ZDEwMTA0ZDAwZjRmNCJ9fX0=");
                ItemMeta halfConfirmMeta = halfConfirm.getItemMeta();
                halfConfirmMeta.setDisplayName("Click to Confirm!");
                halfConfirm.setItemMeta(halfConfirmMeta);
                e.getInventory().setItem(5, halfConfirm);

            }
        }
    }

    /**
     * Event handler for when the buy screen is clicked.
     * @param e event
     */
    @EventHandler
    public void onBuyScreenClick(InventoryClickEvent e) {
        if (e.getInventory().getSize() == 9 &&
                e.getCurrentItem() != null &&
                e.getView().getItem(0) != null &&
                e.getView().getItem(0).getItemMeta().hasLocalizedName() &&
                e.getView().getItem(0).getItemMeta().getLocalizedName().equals("BuyItemScreen")) {

            e.setCancelled(true);

            switch(e.getRawSlot()) {
                case 0: // Back button
                    // Brings the player back to the main page of the store.
                    WorldShop.getStoreManager().openShop((Player) e.getWhoClicked(), 1);
                    break;

                case 5:
                    // Check what the condition of the slot is
                    String name = e.getInventory().getItem(5).getItemMeta().getDisplayName();
                    name = ChatColor.stripColor(name);
                    switch(name) {
                        case "You do not have the required items to buy this!":
                            break;

                        case "Click to Confirm!":
                            // Set the confirm button to Green Check

                            ItemStack fullConfirm = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=");
                            ItemMeta fullConfirmMeta = fullConfirm.getItemMeta();
                            fullConfirmMeta.setDisplayName("Are you sure?");
                            fullConfirm.setItemMeta(fullConfirmMeta);
                            e.getInventory().setItem(5, fullConfirm);
                            break;

                        case "Are you sure?":

                            Inventory inven = e.getInventory();
                            ItemStack forSale = inven.getItem(4);
                            ItemStack wanted = inven.getItem(6);
                            int amountWanted = wanted.getAmount();

                            // Remove pay items from the players inventory
                            e.getWhoClicked().getInventory().removeItem(wanted);

                            String tradeID = e.getInventory().getItem(4).getItemMeta().getLocalizedName();
                            if (tradeID.equals("")) {
                                System.out.println("An error occurred");
                            }

                            WorldShop.getStoreManager().completeTrade((Player) e.getWhoClicked(), Integer.parseInt(tradeID));
                            // Put the player back on page 1 of the shop
                            WorldShop.getStoreManager().openShop((Player) e.getWhoClicked(), 1);

                            break;
                    }
                    break;
            }
        }
    }

    /**
     * Event handler for the current trades page.
     * @param e event
     */
    @EventHandler
    public void onCurrentTradesScreenClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null &&
                e.getInventory().getSize() == 27 &&
                e.getView().getItem(22) != null &&
                e.getView().getItem(22).getItemMeta().hasLocalizedName() &&
                e.getView().getItem(22).getItemMeta().getLocalizedName().equals("ViewCurrentTradesScreen")) {

            e.setCancelled(true);

            switch(e.getRawSlot()) {
                case 11: // View Current Listings Button
                    WorldShop.getStoreManager().viewCurrentListings((Player) e.getWhoClicked(), 1);
                    break;

                case 15: // View Completed Trades Button
                    WorldShop.getStoreManager().viewCompletedTrades((Player) e.getWhoClicked(), 1);
                    break;

                case 22: // Back Button
                    WorldShop.getStoreManager().openShop((Player) e.getWhoClicked(), 1);
            }
        }
    }

    @EventHandler
    public void onCurrentListingsScreenClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null &&
                e.getInventory().getSize() == 36 &&
                e.getView().getItem(31) != null &&
                e.getView().getItem(31).getItemMeta().hasLocalizedName() &&
                e.getView().getItem(31).getItemMeta().getLocalizedName().equals("ViewCurrentListingsScreen")) {

            int currentPage = Integer.parseInt(e.getView().getItem(29).getItemMeta().getLocalizedName());

            e.setCancelled(true);

            if (e.getRawSlot() > 27) {
                switch(e.getRawSlot()) {
                    case 31: // Back Button
                        WorldShop.getStoreManager().manageTrades((Player) e.getWhoClicked());
                        break;

                    case 33: // Next Page
                        if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                            WorldShop.getStoreManager().nextCurrentListingsPage((Player) e.getWhoClicked(), currentPage);
                        }
                        break;

                    case 29: // Prev Page
                        if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                            WorldShop.getStoreManager().prevCurrentListingsPage((Player) e.getWhoClicked(), currentPage);
                        }
                        break;

                    default:
                        break;
                }
            } else {
                if (e.getClick().isLeftClick()) {
                    WorldShop.getStoreManager().viewTrade(WorldShop.getStoreManager().getTradeFromDisplayItem(e.getCurrentItem()), (Player) e.getWhoClicked());
                } else if (e.getClick().isRightClick()) {
                    WorldShop.getStoreManager().removeTradeScreen(WorldShop.getStoreManager().getTradeFromDisplayItem(e.getCurrentItem()), (Player) e.getWhoClicked());
                }
            }
        }
    }

    @EventHandler public void onViewTradeScreen(InventoryClickEvent e) {
        if (e.getCurrentItem() != null &&
                e.getInventory().getSize() == 27 &&
                e.getView().getItem(22) != null &&
                e.getView().getItem(22).getItemMeta().hasLocalizedName() &&
                e.getView().getItem(22).getItemMeta().getLocalizedName().equals("ViewTradeScreen")) {

            e.setCancelled(true);

            switch(e.getRawSlot()) {
                case 22: // Back button
                    WorldShop.getStoreManager().viewCurrentListings((Player) e.getWhoClicked(), 1);
                    break;

                default:
                    break;
            }

        }
    }

    @EventHandler public void onDeleteTradeScreen(InventoryClickEvent e) {
        if (e.getCurrentItem() != null &&
                e.getInventory().getSize() == 18 &&
                e.getView().getItem(11).getItemMeta().hasLocalizedName() &&
                e.getView().getItem(11).getItemMeta().getLocalizedName().equals("RemoveTradeScreen")) {

            e.setCancelled(true);

            switch(e.getRawSlot()) {
                case 11:
                    WorldShop.getStoreManager().deleteTrade(WorldShop.getStoreManager().getTradeFromDisplayItem(e.getInventory().getItem(4)).getTradeID());
                    // Put the player back on page 1 of current listings
                    WorldShop.getStoreManager().viewCurrentListings((Player) e.getWhoClicked(), 1);
                    break;

                case 15:
                   WorldShop.getStoreManager().viewCurrentListings((Player) e.getWhoClicked(), 1);
                   break;

                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onCompletedTradesScreenClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null &&
                e.getInventory().getSize() == 36 &&
                e.getView().getItem(31) != null &&
                e.getView().getItem(31).getItemMeta().hasLocalizedName() &&
                e.getView().getItem(31).getItemMeta().getLocalizedName().equals("ViewCompletedTradesScreen")) {

            int currentPage = Integer.parseInt(e.getView().getItem(29).getItemMeta().getLocalizedName());

            e.setCancelled(true);

           if (e.getRawSlot() > 27) {
               switch (e.getRawSlot()) {
                   case 31: // Back Button
                       WorldShop.getStoreManager().manageTrades((Player) e.getWhoClicked());
                       break;

                   case 33: // Next Page
                       if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                           WorldShop.getStoreManager().nextCompletedTradesPage((Player) e.getWhoClicked(), currentPage);
                       }
                       break;

                   case 29: // Prev Page
                       if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                           WorldShop.getStoreManager().prevCompletedTradesPage((Player) e.getWhoClicked(), currentPage);
                       }
                       break;

                   default:
                       break;
               }
           } else {

               Player p = (Player) e.getWhoClicked();
               if (p.getInventory().firstEmpty() != -1) {

                   // Update the database
                   WorldShop.getDatabase().update("UPDATE pickups SET collected = ?, time_collected = ? WHERE trade_id = ? AND player_uuid = ?;",
                           new Object[]{true, System.currentTimeMillis(), Integer.parseInt(e.getCurrentItem().getItemMeta().getLocalizedName()), p.getUniqueId().toString()},
                           new int[]{Types.BOOLEAN, Types.BIGINT, Types.INTEGER, Types.VARCHAR}
                   );
//                   try {
//                       PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("UPDATE pickups SET collected = ?, time_collected = ? WHERE trade_id = ? AND player_uuid = ?;");
//                       ps.setBoolean(1, true);
//                       ps.setLong(2, System.currentTimeMillis());
//                       ps.setInt(3, Integer.parseInt(e.getCurrentItem().getItemMeta().getLocalizedName()));
//                       ps.setString(4, ((Player) e.getWhoClicked()).getUniqueId().toString());
//
//                       ps.executeUpdate();
//
//
//                   } catch (SQLException ev) {
//                       ev.printStackTrace();
//                   }

                   e.getCurrentItem().setItemMeta(null);

                   p.getInventory().addItem(e.getCurrentItem());
                   e.getInventory().removeItem(e.getCurrentItem());

               } else {
                   p.sendMessage(ChatColor.RED + "There is not enough space in your inventory to collect the item! Please make some space!");
               }
           }
        }
    }

    @EventHandler
    public void onSorryItUpdatedScreenClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null &&
                e.getInventory().getSize() == 27 &&
                e.getView().getItem(13) != null &&
                e.getView().getItem(13).getItemMeta().hasLocalizedName() &&
                e.getView().getItem(13).getItemMeta().getLocalizedName().equals("SorryItUpdatedScreen")) {

            Player player = (Player) e.getWhoClicked();
            WorldShop.getStoreManager().openShop(player, 1);
        }
    }


    /**
     * Event handler to update whether the player has the shop open or not.
     * This is used for when a listing sells and multiple people have the same trade listing open.
     * @param e event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (WorldShop.getStoreManager().playersWithStoreOpen.contains(e.getPlayer())) {
            WorldShop.getStoreManager().playersWithStoreOpen.remove(e.getPlayer());
        }
    }
}
