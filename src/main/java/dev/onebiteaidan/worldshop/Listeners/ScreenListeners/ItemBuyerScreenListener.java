package dev.onebiteaidan.worldshop.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.Listeners.ScreenListener;
import dev.onebiteaidan.worldshop.GUI.Screens.ItemBuyerScreen;
import dev.onebiteaidan.worldshop.GUI.Screens.MainShopScreen;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

public class ItemBuyerScreenListener extends ScreenListener {

    /**
     * Event handler for when the buy screen for an item is opened.
     * This updater runs to set the initial status of the checkbox based on if the player has enough of the price item
     * @param event called on Inventory Open
     */
    @Override
    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof ItemBuyerScreen) {
            ItemBuyerScreen holder = (ItemBuyerScreen) event.getInventory().getHolder();

            try {
                int numItemsPlayerInventory = Utils.getNumOfItems(holder.getPlayer(), holder.getInventory().getItem(6));
                int numItemsToPurchase = Objects.requireNonNull(holder.getInventory().getItem(6)).getAmount();

                // Check if player has the required items to buy the item
                if (numItemsPlayerInventory >= numItemsToPurchase) {

                    // Change confirm button to Yellow Check
                    String halfConfirmURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmNDI1YjRkYjdkNjJiMjAwZTg5YzAxM2U0MjFhOWUxMTBiZmIyN2YyZDhiOWY1ODg0ZDEwMTA0ZDAwZjRmNCJ9fX0=";

                    TextComponent halfConfirmTitle = text("Click to Confirm!");

                    ItemStack halfConfirm = Utils.createButtonItem(Utils.createSkull(halfConfirmURL), halfConfirmTitle, null);
                    holder.getInventory().setItem(5, halfConfirm);
                    // TODO: Im wondering if the update method will have to be called on the inventory after setting the item from the holder and not the event.
                }
            } catch (NullPointerException exception) {
                Logger.logStacktrace(exception);
            }
        }
    }

    @Override
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof ItemBuyerScreen) {
            ItemBuyerScreen holder = (ItemBuyerScreen) event.getInventory().getHolder();

            event.setCancelled(true);

            switch(event.getRawSlot()) {
                case 0: // Back button
                    // Brings the player back to the main page of the store.
                    new MainShopScreen(holder.getPlayer()).openScreen(1);
                    break;

                case 5:
                    // Check what the condition of the slot is
                    try {
                        TextComponent name = (TextComponent) Objects.requireNonNull(holder.getInventory().getItem(5)).displayName();

                        switch(name.content()) {
                            case "You do not have the required items to buy this!":
                                break;

                            case "Click to Confirm!":
                                // Set the confirm button to Green Check
                                String fullConfirmTitleURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";

                                TextComponent fullConfirmTitle = text("Are you sure?");

                                ItemStack fullConfirm = Utils.createButtonItem(Utils.createSkull(fullConfirmTitleURL), fullConfirmTitle, null);
                                holder.getInventory().setItem(5, fullConfirm);
                                break;

                            case "Are you sure?":
                                Inventory inventory = holder.getInventory();
                                ItemStack forSale = inventory.getItem(4);
                                ItemStack wanted = inventory.getItem(6);
                                assert wanted != null;
                                int amountWanted = wanted.getAmount(); // TODO: Find out if extra stuff needs to happen here. Are the items actually being removed?

                                // Remove pay items from the players inventory
                                event.getWhoClicked().getInventory().removeItem(wanted);

                                // Complete the trade
                                WorldShop.getStoreManager().completeTrade(holder.getTrade(), (Player) event.getWhoClicked());

                                // Put the player back on page 1 of the shop
                                new MainShopScreen(holder.getPlayer()).openScreen(1);
                                break;
                        }
                        break;
                    } catch (NullPointerException exception) {
                        Logger.logStacktrace(exception);
                    }
            }
        }
    }
}