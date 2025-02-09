package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.GUI.GUI;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.GUI.Screen;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static dev.onebiteaidan.worldshop.GUI.StaticItems.*;
import static net.kyori.adventure.text.Component.text;


public class ItemSellerScreen extends Screen {

    public enum ConfirmStatus {
        CANNOT_CONFIRM,     // Red
        CAN_CONFIRM,        // Yellow
        DOUBLE_CONFIRM      // Green
    }

    // Normal class attributes
    ConfirmStatus confirmStatus;
    int priceAmount;
    ItemStack sellItem;
    ItemStack priceItem;

    public ItemSellerScreen(Player player) {
        setPlayer(player);
        this.confirmStatus = ConfirmStatus.CANNOT_CONFIRM;
        this.priceAmount = 1;

        Component title = text("What would you like to sell?")
                .color(NamedTextColor.DARK_GRAY);

        GUI gui = new GUI(27, title, "ItemSellerScreen");
        plugin.getServer().getPluginManager().registerEvents(gui, plugin);
        setGUI(gui);

        // Initialize screen
        initializeScreen();
    }

    @Override
    protected void initializeScreen() {
        // Dividers
        getInventory().setItem(1, divider);
        getInventory().setItem(10, divider);
        getInventory().setItem(19, divider);


        // Confirm the trade button (cannot confirm is the default state)
        Button cannotConfirmButton = new Button(cannotConfirmButtonItem, () -> {
            // fixme: How do I do the status handling here?
            try {
                switch (getConfirmStatus()) {
                    case CANNOT_CONFIRM:
                        break;

                    case CAN_CONFIRM:
                        // Set the confirm button to Green Check
                        updateStatus();
                        return;

                    case DOUBLE_CONFIRM:
                        Inventory inventory = getInventory();
                        ItemStack forSale = inventory.getItem(12);
                        ItemStack inReturn = inventory.getItem(15);

                        // Remove first occurrence of a repeat item stack in the players inventory
                        if (player.getInventory().contains(forSale)) {
                            player.getInventory().setItem(player.getInventory().first(forSale), null);
                        } else {
                            WorldShop.getPlugin(WorldShop.class).getLogger().severe("Player attempted to sell an item without it being in their inventory");
                            break;
                        }

                        WorldShop.getStoreManager().createTrade(new Trade(player, forSale, inReturn));

                        // Brings the player back to the main page of the store.
                        new MainShopScreen(player).openScreen(1);
                        break;
                }
            } catch (NullPointerException exception) {
                Logger.logStacktrace(exception);
            }
        });
        gui.addButton(0, cannotConfirmButton);


        // Item player wants to sell
        Button emptySellItemButton = new Button(emptySellItem, () -> {
            player.sendMessage("Resetting sell slot");
            resetSellSlot();
        });
        gui.addButton(12, emptySellItemButton);


        // Increase the number of items the player wants in return
        String increasePriceButtonURL = "http://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f";
        TextComponent increasePriceButtonTitle = text("Increase Price by 1");
        ItemStack increasePriceButtonItem = Utils.createButtonItem(Utils.createSkull(increasePriceButtonURL), increasePriceButtonTitle, null);
        Button increasePriceButton = new Button(increasePriceButtonItem, () -> {
            if (!isSellSlotEmpty()) {
                player.sendMessage("Increasing price");
                increasePrice();
            }
        });
        gui.addButton(14, increasePriceButton);


        // Item player wants to receive
        Button emptyPriceItemButton = new Button(emptyPriceItem, () -> {
            player.sendMessage("Resetting price slot");
            resetPriceSlot();
        });
        gui.addButton(15, emptyPriceItemButton);


        // Decrease the number of items the player wants in return
        String decreasePriceButtonURL = "http://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46";
        TextComponent decreasePriceButtonTitle = text("Decrease Price by 1");
        ItemStack decreasePriceButtonItem = Utils.createButtonItem(Utils.createSkull(decreasePriceButtonURL), decreasePriceButtonTitle, null);
        Button decreasePriceButton = new Button(decreasePriceButtonItem, () -> {
            if (!isPriceSlotEmpty()) {
                player.sendMessage("Decreasing price");
                decreasePrice();
            }
        });
        gui.addButton(16, decreasePriceButton);


        // Go back to the main menu button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButtonItem = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        Button backButton = new Button(backButtonItem, () -> {
           player.sendMessage("Clicked the back button");
            new MainShopScreen(player).openScreen(1);
        });
        gui.addButton(18, backButton);
    }

    @Override
    public void update() {
        // Do nothing
    }

    public void setSellItem(ItemStack sellItem) {
        // Ensure the clicked item is not a copy of the divider item stack
        if (sellItem.equals(divider)) {
            return;
        }

        // Replace the current sellItem or emptySellItem placeholder with new sellItem
        this.sellItem = sellItem;
        getInventory().setItem(12, sellItem);

        // Reset item amount to zero
        this.priceAmount = 1;

        // Update the confirm button status
        updateStatus();
    }

    public void setPriceItem(ItemStack priceItem) {
        // Ensure the clicked item is not a copy of the divider item stack
        if (priceItem.equals(divider)) {
            return;
        }

        // Replace the current priceItem or emptyPriceItem placeholder with new priceItem
        this.priceItem = priceItem;
        priceItem.setAmount(1);
        getInventory().setItem(15, priceItem);

        // Reset item amount to zero
        this.priceAmount = 1;

        // Update the confirm button status
        updateStatus();
    }

    public boolean isSellSlotEmpty() {
        if (sellItem == null) {
            return false;
        }
        return sellItem.equals(emptySellItem);
    }

    public boolean isPriceSlotEmpty() {
        if (priceItem == null) {
            return false;
        }
        return priceItem.equals(emptyPriceItem);
    }

    public void resetSellSlot() {
        // Replace the current sell item with the emptySellItem placeholder
        // ItemStacks that represent no item present
        TextComponent emptySellItemTitle = text("Left click the item in your inventory you want to sell!");
        ItemStack emptySellItem = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, emptySellItemTitle, null);

        this.getInventory().setItem(12, emptySellItem);

        // Reset item amount to zero
        this.priceAmount = 1;

        // Update the status of the confirm button
        this.confirmStatus = ConfirmStatus.CANNOT_CONFIRM;
        updateStatus();
    }

    public void resetPriceSlot() {
        // Replace the current price item with the emptyPriceItem placeholder
        TextComponent emptyPriceItemTitle = text("Right click the item in your inventory you want to receive in trade!");
        ItemStack emptyPriceItem = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, emptyPriceItemTitle, null);

        this.getInventory().setItem(15, emptyPriceItem);

        // Reset item amount to zero
        this.priceAmount = 1;

        // Update the status of the confirm button
        this.confirmStatus = ConfirmStatus.CANNOT_CONFIRM;
        updateStatus();
    }

    public void increasePrice() {
        if (priceAmount < 64) {
            priceAmount++;
            Objects.requireNonNull(getInventory().getItem(15)).setAmount(priceAmount);
        }
    }

    public void decreasePrice() {
        if (priceAmount > 1) {
            priceAmount--;
            Objects.requireNonNull(getInventory().getItem(15)).setAmount(priceAmount);
        }
    }

    public ConfirmStatus getConfirmStatus() {
        return this.confirmStatus;
    }

    /**
     * Updates the status of the confirm button based on if there are items placed in the sell item slot or not.
     */
    public void updateStatus() {
        // Check if items are placed in each slot
        if (!Objects.equals(getInventory().getItem(12), emptySellItem) && !Objects.equals(getInventory().getItem(15), emptyPriceItem)) {
            // Both slots have valid items

            // Find out what status we need to update to
            if (confirmStatus == ConfirmStatus.CANNOT_CONFIRM) {
                confirmStatus = ConfirmStatus.CAN_CONFIRM; // Goes to Yellow Checkmark state
                Button canConfirmButton = new Button(canConfirmButtonItem, () -> {
                   player.sendMessage("Can confirm button item");
                });
                gui.addButton(0, canConfirmButton);

            } else if (confirmStatus == ConfirmStatus.CAN_CONFIRM) {
                confirmStatus = ConfirmStatus.DOUBLE_CONFIRM; // Goes to Green Checkmark state
                Button doubleConfirmButton = new Button(doubleConfirmButtonItem, () -> {
                    player.sendMessage("Double confirm button item");
                });
                gui.addButton(0, doubleConfirmButton);
            }

        } else {
            // Go to Red X state
            confirmStatus = ConfirmStatus.CANNOT_CONFIRM;
            Button cannotConfirmButton = new Button(cannotConfirmButtonItem, () -> {
                player.sendMessage("Cannot confirm button item");
            });
            gui.addButton(0, cannotConfirmButton);
        }
    }
}