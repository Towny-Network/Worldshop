package dev.onebiteaidan.worldshop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.calledmethods.qual.EnsuresCalledMethodsVarArgs;

import java.util.ArrayList;

public class StoreListener implements Listener {

    @EventHandler
    public void onWorldShopScreenClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("WorldShop")) {

            e.setCancelled(true); //Todo: this needs error checking to make sure it's not just a chest with the name "WorldsShop"

            int currentPage = Integer.parseInt(e.getInventory().getItem(45).getItemMeta().getLocalizedName());

            switch(e.getRawSlot()) {
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

                default:
                    // Open buy screen for the item that was clicked on


            }
        }
    }

    @EventHandler
    public void testListener(InventoryClickEvent e) {
        System.out.println(e.getInventory().toString() + " SLOT: " + String.valueOf(e.getRawSlot()));
    }

    @EventHandler
    public void onSellScreenClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("What would you like to sell?")) {

            // Checks if the player is clicking items in their inventory or items in the gui.
            // Also prevents shift clicking into the area they aren't supposed to.
            if (e.getRawSlot() < 27 && !e.getClick().isShiftClick()) { //TODO: Players can still shift click items into the sell screen menu.
                e.setCancelled(true); //Todo: this needs error checking to make sure it's not just a chest with the name "What would you like to sell?"
            }

            //Todo: Flush out how the player will put the items into the respective itemToSell and itemInReturn slots.
            // Current ideas include:
            // -Using left and right clicks to determine which one (the only downside to this is that the player cannot manage their local inventory
            //  while in the shop screen. What if they want to sell only half a stack. Or only 1 item out of a whole stack.)
            // -Drag and drop itemstacks into the spot they want to put them into.
            // -Click on whatever space they want to put it in (sell or inReturn) to enter a mode then have them double click the itemstack they want put in.


            switch(e.getRawSlot()) {
                case 0: // Is this necessary for our implementation
                    //Todo:  Checks if both itemstack spots are filled before confirming the trade


                    // Checks if this is the first click
                    if (e.getInventory().getItem(0).getType().equals(Material.YELLOW_CONCRETE_POWDER)) {

                        // Confirm the trade button
                        ItemStack confirmTradeButton = new ItemStack(Material.LIME_CONCRETE_POWDER);
                        ItemMeta confirmTradeButtonMeta = confirmTradeButton.getItemMeta();
                        confirmTradeButtonMeta.setDisplayName("Confirm");
                        confirmTradeButton.setItemMeta(confirmTradeButtonMeta);

                        e.getInventory().setItem(0, confirmTradeButton);

                    } else { // Should be second click at this point. So add the item to the store.

                        Inventory inven = e.getInventory();
                        ItemStack forSale = inven.getItem(12);
                        ItemStack wanted = inven.getItem(15);
                        int amountWanted = wanted.getAmount();

                        WorldShop.getStoreManager().addToStore(forSale, wanted, amountWanted, (Player) e.getWhoClicked());
                    }

                case 18: // Back button
                    // Brings the player back to the main page of the store.
                    WorldShop.getStoreManager().openShop((Player) e.getWhoClicked(), 1);
                    break;



                case 14: // Reset price to 1
                    // Doing something other than back button or confirm; reset the confirm button
                    resetConfirmButton(e.getInventory());

                    e.getInventory().getItem(15).setAmount(1);
                    break;

                case 16: // Set the price
                    // Doing something other than back button or confirm; reset the confirm button
                    resetConfirmButton(e.getInventory());

                    Inventory savedInven = e.getInventory();

                    Player player = (Player) e.getWhoClicked();
                    player.closeInventory();

                    player.sendMessage("How how many of these would you like to sell this item for?");



                    // TODO: make an sign interface to entering the number of items wanted.

                    break;

                default: // Right and left clicks out of the gui set the wanted and forsale item
                    // Check if item exists here and also check if it was a left or right click
                    //TODO: This needs a way to make it so players can interact w/ their inventory but not the chestgui.
                    // So they can split up stacks n stuff


                    if (!e.getCurrentItem().getType().equals(Material.AIR)) {
                        // Doing something other than back button or confirm; reset the confirm button
                        resetConfirmButton(e.getInventory());

                        // Determine if it's a right or left click
                        if (e.getClick().isLeftClick()) { // Set the item we are selling
                            e.getInventory().setItem(12, e.getCurrentItem());

                        } else if (e.getClick().isRightClick()) {
                            e.getInventory().setItem(15, e.getCurrentItem());
                        }
                    }

                    break;

            }
        }
    }

    private void resetConfirmButton(Inventory inventory) {
        // Confirm the trade button
        ItemStack confirmTradeButton = new ItemStack(Material.YELLOW_CONCRETE_POWDER);
        ItemMeta confirmTradeButtonMeta = confirmTradeButton.getItemMeta();
        confirmTradeButtonMeta.setDisplayName("Confirm");
        confirmTradeButton.setItemMeta(confirmTradeButtonMeta);

        inventory.setItem(0, confirmTradeButton);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        ArrayList<Player> playersInStore = WorldShop.getStoreManager().playersWithStoreOpen;
        playersInStore.remove(e.getPlayer());
    }
}
