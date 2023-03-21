package dev.onebiteaidan.worldshop;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StoreListener implements Listener {

    @EventHandler
    public void onWorldShopScreenClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("WorldShop")) {

            e.setCancelled(true); //Todo: this needs error checking to make sure it's not just a chest with the name "WorldsShop"

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

    @EventHandler
    public void onSellScreenClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("What would you like to sell?")) {
            
            e.setCancelled(true); //Todo: this needs error checking to make sure it's not just a chest with the name "What would you like to sell?"

            switch(e.getRawSlot()) {
                case 0: // Submit button
                    // Check what the condition of the slot is
                    String name = e.getInventory().getItem(0).getItemMeta().getDisplayName();
                    name = ChatColor.stripColor(name);
                    switch(name) {
                        case "You cannot confirm until you have put in a sell item and a price item!":
                            break;

                        case "Click to Confirm!":
                            // Set the confirm buttton to Green Check

                            ItemStack fullConfirm = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=");
                            ItemMeta fullConfirmMeta = fullConfirm.getItemMeta();
                            fullConfirmMeta.setDisplayName("Are you sure?");
                            fullConfirm.setItemMeta(fullConfirmMeta);
                            e.getInventory().setItem(0, fullConfirm);
                            return;

                        case "Are you sure?":
                            Inventory inven = e.getInventory();
                            ItemStack forSale = inven.getItem(12);
                            ItemStack wanted = inven.getItem(15);
                            int amountWanted = wanted.getAmount();

                            // Remove first occurrence of a repeat itemstack in the players inventory
                            if (e.getWhoClicked().getInventory().contains(forSale)) {
                                e.getWhoClicked().getInventory().setItem(e.getWhoClicked().getInventory().first(forSale), null);
                            } else {
                                e.getWhoClicked().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Something went wrong. Please open a ticket on the Discord. ERROR CODE: WS0002");
                                WorldShop.getPlugin(WorldShop.class).getLogger().severe("Player attempted to sell an item without it being in their inventory");
                                break;
                            }

                            WorldShop.getStoreManager().addToStore(forSale, wanted, amountWanted, (Player) e.getWhoClicked());

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
                    ItemStack priceButton = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                    ItemMeta priceButtonMeta = priceButton.getItemMeta();
                    priceButtonMeta.setDisplayName("Right Click the item in your inventory you want to receive in trade!");
                    priceButton.setItemMeta(priceButtonMeta);
                    e.getInventory().setItem(15, priceButton);
                    break;


                default:
                    // Check if item exists here and also check if it was a left or right click
                    if (!e.getCurrentItem().getType().equals(Material.AIR) && !e.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {


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


    @EventHandler
    public void onOpenBuyScreen(InventoryOpenEvent e) {
        if (e.getInventory() != null && e.getView().getItem(0).getItemMeta().hasLocalizedName() && e.getView().getItem(0).getItemMeta().getLocalizedName().equals("BuyItemScreen")) {
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

    @EventHandler
    public void onBuyScreenClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getItem(0).getItemMeta().hasLocalizedName() && e.getView().getItem(0).getItemMeta().getLocalizedName().equals("BuyItemScreen")) {

            e.setCancelled(true);

            // Check if player has the required items to buy the item
            if (Utils.getNumOfItems((Player) e.getWhoClicked(), e.getInventory().getItem(6)) >= e.getInventory().getItem(6).getAmount()) {

                // Change confirm button to Yellow Check
                ItemStack halfConfirm = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmNDI1YjRkYjdkNjJiMjAwZTg5YzAxM2U0MjFhOWUxMTBiZmIyN2YyZDhiOWY1ODg0ZDEwMTA0ZDAwZjRmNCJ9fX0=");
                ItemMeta halfConfirmMeta = halfConfirm.getItemMeta();
                halfConfirmMeta.setDisplayName("Click to Confirm!");
                halfConfirm.setItemMeta(halfConfirmMeta);
                e.getInventory().setItem(5, halfConfirm);

            }

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
                            // Set the confirm buttton to Green Check

                            ItemStack fullConfirm = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=");
                            ItemMeta fullConfirmMeta = fullConfirm.getItemMeta();
                            fullConfirmMeta.setDisplayName("Are you sure?");
                            fullConfirm.setItemMeta(fullConfirmMeta);
                            e.getInventory().setItem(5, fullConfirm);
                            return;

                        case "Are you sure?":

                            //Todo: needs finishing
                            Inventory inven = e.getInventory();
                            ItemStack forSale = inven.getItem(4);
                            ItemStack wanted = inven.getItem(6);
                            int amountWanted = wanted.getAmount();

                            // Remove pay items from the players inventory
                            HashMap<Integer, ItemStack> remainder = e.getWhoClicked().getInventory().removeItem(wanted);
                            if (!remainder.isEmpty()) {
                                // This case should hopefully never be reached
                                e.getWhoClicked().sendMessage(ChatColor.RED + "Something Wrong Happened! Please open a ticket on our discord. ERROR CODE: WS0001");
                                e.getWhoClicked().closeInventory();
                            }

                            e.getWhoClicked().getInventory().addItem(forSale);
                            WorldShop.getStoreManager().buy(WorldShop.getStoreManager().getTradeFromTradeID(e.getInventory().getItem(4).getItemMeta().getLocalizedName()), (Player) e.getWhoClicked());


                            // Brings the player back to the main page of the store.
                            WorldShop.getStoreManager().openShop((Player) e.getWhoClicked(), 1);
                            break;
                    }

                    break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        ArrayList<Player> playersInStore = WorldShop.getStoreManager().playersWithStoreOpen;
        playersInStore.remove(e.getPlayer());
    }
}
