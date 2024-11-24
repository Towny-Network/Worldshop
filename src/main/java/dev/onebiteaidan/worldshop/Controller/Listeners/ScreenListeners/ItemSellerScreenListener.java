package dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.ItemSellerScreen;
import dev.onebiteaidan.worldshop.View.Screens.MainShopScreen;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static dev.onebiteaidan.worldshop.Model.StaticItems.emptyPriceItem;

public class ItemSellerScreenListener extends ScreenListener {
    @Override
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof ItemSellerScreen) {
            ItemSellerScreen holder = (ItemSellerScreen) event.getInventory().getHolder();

            event.setCancelled(true);



            switch (event.getRawSlot()) {
                case 0: // Submit button
                    // Check what the condition of the slot is
                    try {
                        switch (holder.getConfirmStatus()) {
                            case CANNOT_CONFIRM:
                                break;

                            case CAN_CONFIRM:
                                // Set the confirm button to Green Check
                                holder.updateStatus();
                                return;

                            case DOUBLE_CONFIRM:
                                Inventory inventory = holder.getInventory();
                                ItemStack forSale = inventory.getItem(12);
                                ItemStack inReturn = inventory.getItem(15);

                                // Remove first occurrence of a repeat item stack in the players inventory
                                if (holder.getPlayer().getInventory().contains(forSale)) {
                                    holder.getPlayer().getInventory().setItem(holder.getPlayer().getInventory().first(forSale), null);
                                } else {
                                    WorldShop.getPlugin(WorldShop.class).getLogger().severe("Player attempted to sell an item without it being in their inventory");
                                    break;
                                }

                                WorldShop.getStoreManager().createTrade(new Trade(holder.getPlayer(), forSale, inReturn));

                                // Brings the player back to the main page of the store.
                                new MainShopScreen(holder.getPlayer()).openScreen(1);
                                break;
                        }
                    } catch (NullPointerException exception) {
                        Logger.logStacktrace(exception);
                    }

                    break;


                case 18: // Back button
                    // Brings the player back to the main page of the store.
                    new MainShopScreen(holder.getPlayer()).openScreen(1);
                    break;


                case 14: // Increase Price
                    if (!Objects.equals(holder.getInventory().getItem(15), emptyPriceItem)) {
                        holder.increasePrice();
                    }
                    break;


                case 16: // Decrease Price
                    if (!Objects.equals(holder.getInventory().getItem(15), emptyPriceItem)) {
                        holder.decreasePrice();
                    }
                    break;


                case 12: // Reset/remove sell item to it's placeholder
                    holder.resetSellSlot();
                    break;


                case 15: // Reset/remove price item to it's placeholder
                    holder.resetPriceSlot();
                    break;


                default:
                    // Check that the item is being clicked from inside the player's inventory
                    if (event.getClickedInventory() instanceof ItemSellerScreen) {
                        break;
                    }

                    if (event.getCurrentItem() != null) {
                        // Determine if it's a right or left click
                        if (event.getClick().isLeftClick()) { // Set the item we are selling
                            holder.setSellItem(event.getCurrentItem());

                        } else if (event.getClick().isRightClick()) { // Set the item we want to receive
                            holder.setPriceItem(event.getCurrentItem());
                        }
                    }
                    break;
            }
        }
    }
}
