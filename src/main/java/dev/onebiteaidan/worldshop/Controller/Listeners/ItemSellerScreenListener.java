package dev.onebiteaidan.worldshop.Controller.Listeners;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.View.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.ItemSellerScreen;
import dev.onebiteaidan.worldshop.View.Screens.MainShopScreen;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

public class ItemSellerScreenListener extends ScreenListener {
    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof ItemSellerScreen) {
            ItemSellerScreen holder = (ItemSellerScreen) event.getInventory().getHolder();

            event.setCancelled(true);

            ItemStack itemForSale;

            switch (event.getRawSlot()) {
                case 0: // Submit button
                    // Check what the condition of the slot is
                    try {
                        TextComponent name = (TextComponent) Objects.requireNonNull(holder.getInventory().getItem(0)).displayName();

                        switch (name.content()) {
                            case "You cannot confirm until you have put in a sell item and a price item!":
                                break;

                            case "Click to Confirm!":
                                // Set the confirm button to Green Check
                                String fullConfirmTitleURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";

                                TextComponent fullConfirmTitle = text("Are you sure?");

                                ItemStack fullConfirm = Utils.createButtonItem(Utils.createSkull(fullConfirmTitleURL), fullConfirmTitle, null);
                                holder.getInventory().setItem(0, fullConfirm);
                                return;

                            case "Are you sure?":
                                Inventory inventory = holder.getInventory();
                                ItemStack forSale = inventory.getItem(12);
                                ItemStack inReturn = inventory.getItem(15);

                                // Remove first occurrence of a repeat itemstack in the players inventory
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
                        Utils.logStacktrace(exception);
                    }

                    break;


                case 18: // Back button
                    // Brings the player back to the main page of the store.
                    new MainShopScreen(holder.getPlayer()).openScreen(1);
                    break;


                case 14: // Increase Price
                    itemForSale = Objects.requireNonNull(holder.getInventory().getItem(15));

                    // Make sure the price item is populated and is less than 64.
                    if (!itemForSale.getType().equals(Material.RED_STAINED_GLASS_PANE) && itemForSale.getAmount() < 64) {
                        holder.getInventory().setItem(itemForSale.getAmount() + 1, itemForSale);
                    }
                    break;


                case 16: // Decrease Price
                    itemForSale = Objects.requireNonNull(holder.getInventory().getItem(15));

                    // Make sure the price item is populated and is more than 1.
                    if (!itemForSale.getType().equals(Material.RED_STAINED_GLASS_PANE) && itemForSale.getAmount() > 1) {
                        holder.getInventory().setItem(itemForSale.getAmount() - 1, itemForSale);
                    }
                    break;


                case 12: // Reset/remove buy item
                    // Changes current itemstack back to the original item player wants to sell
                    TextComponent blankItemSpotTitle = text("Press ")
                            .append(Component.keybind().keybind("key.break"))
                            .append(text(" the item in your inventory you want to sell!"));

                    ItemStack blankItemSpotButton = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, blankItemSpotTitle, null);
                    holder.getInventory().setItem(12, blankItemSpotButton);
                    break;


                case 15: // Reset/remove price item
                    // Item player wants to receive in trade
                    TextComponent priceButtonTitle = text("Press ")
                            .append(Component.keybind().keybind("key.place"))
                            .append(text(" the item in your inventory you want to receive in trade!"));

                    ItemStack priceButton = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, priceButtonTitle, null);
                    holder.getInventory().setItem(15, priceButton);
                    break;


                default:
                    // Check that the item is being clicked from inside the player's inventory
                    if (event.getClickedInventory() instanceof ItemSellerScreen) {
                        break;
                    }

                    if (event.getCurrentItem() != null) {
                        // Determine if it's a right or left click
                        if (event.getClick().isLeftClick()) { // Set the item we are selling
                            event.getInventory().setItem(12, event.getCurrentItem());

                        } else if (event.getClick().isRightClick()) {
                            ItemStack curr = new ItemStack(event.getCurrentItem());
                            curr.setAmount(1);
                            holder.getInventory().setItem(15, curr);
                        }
                    }
                    break;
            }


            // Check if sell and price slots are filled
            ItemStack sellItem = Objects.requireNonNull(holder.getInventory().getItem(12));
            ItemStack priceItem = Objects.requireNonNull(holder.getInventory().getItem(15));

            if (!sellItem.getType().equals(Material.RED_STAINED_GLASS_PANE) &&
                    !((TextComponent) sellItem.displayName()).content().equals("Left click the item in your inventory you want to sell!") &&
                    !priceItem.getType().equals(Material.RED_STAINED_GLASS_PANE) &&
                    !((TextComponent) priceItem.displayName()).content().equals("Left click the item in your inventory you want to sell!")) {

                // Change confirm button to Yellow Check
                String halfConfirmURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmNDI1YjRkYjdkNjJiMjAwZTg5YzAxM2U0MjFhOWUxMTBiZmIyN2YyZDhiOWY1ODg0ZDEwMTA0ZDAwZjRmNCJ9fX0=";

                TextComponent halfConfirmTitle = text("Click to confirm!");

                ItemStack halfConfirm = Utils.createButtonItem(Utils.createSkull(halfConfirmURL), halfConfirmTitle, null);
                holder.getInventory().setItem(0, halfConfirm);
            }
        }
    }
}
