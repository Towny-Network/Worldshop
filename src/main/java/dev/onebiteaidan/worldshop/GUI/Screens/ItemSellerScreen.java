package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.GUI.AbstractMenu;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static dev.onebiteaidan.worldshop.GUI.StaticItems.*;
import static net.kyori.adventure.text.Component.text;


public class ItemSellerScreen extends AbstractMenu {

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

    public ItemSellerScreen() {
        super(text("What would you like to sell?").color(NamedTextColor.DARK_GRAY), 27);

        this.confirmStatus = ConfirmStatus.CANNOT_CONFIRM;
        this.priceAmount = 1;

        // Initialize screen
        initializeScreen();
    }

    private void initializeScreen() {
        // Dividers
//        getInventory().setItem(1, divider);
//        getInventory().setItem(10, divider);
//        getInventory().setItem(19, divider);


        // RED is default case
        setButton(0, new Button(cannotConfirmButtonItem, (InventoryClickEvent event) -> {

        }));


        // Item player wants to sell
        setButton(12, new Button(emptySellItem, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Resetting sell slot");
            resetSellSlot();
        }));


        // Increase the number of items the player wants in return
        String increasePriceButtonURL = "http://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f";
        TextComponent increasePriceButtonTitle = text("Increase Price by 1");
        ItemStack increasePriceButtonItem = Utils.createButtonItem(Utils.createSkull(increasePriceButtonURL), increasePriceButtonTitle, null);
        setButton(14, new Button(increasePriceButtonItem, (InventoryClickEvent event) -> {
            if (!isSellSlotEmpty()) {
                Player player = (Player) event.getWhoClicked();
                player.sendMessage("Increasing price");
                increasePrice();
            }
        }));


        // Item player wants to receive
        setButton(15, new Button(emptyPriceItem, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Resetting price slot");
            resetPriceSlot();
        }));


        // Decrease the number of items the player wants in return
        String decreasePriceButtonURL = "http://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46";
        TextComponent decreasePriceButtonTitle = text("Decrease Price by 1");
        ItemStack decreasePriceButtonItem = Utils.createButtonItem(Utils.createSkull(decreasePriceButtonURL), decreasePriceButtonTitle, null);
        setButton(16, new Button(decreasePriceButtonItem, (InventoryClickEvent event) -> {
            if (!isPriceSlotEmpty()) {
                Player player = (Player) event.getWhoClicked();
                player.sendMessage("Decreasing price");
                decreasePrice();
            }
        }));


        // Go back to the main menu button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButtonItem = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        setButton(18, new Button(backButtonItem, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked the back button");
            WorldShop.getMenuManager().openMenu(player, new MainShopScreen(player));
        }));
    }

    public void setSellItem(ItemStack sellItem) {
        // Ensure the clicked item is not a copy of the divider item stack
        if (sellItem.equals(divider)) {
            return;
        }

        // Replace the current sellItem or emptySellItem placeholder with new sellItem
        this.sellItem = sellItem;
        inventory.setItem(12, sellItem);

        // Reset item amount to zero
        this.priceAmount = 1;

        // Update the confirm button status
        if (!Objects.equals(inventory.getItem(12), emptySellItem) && !Objects.equals(inventory.getItem(15), emptyPriceItem)) {
            // Both slots have valid items
            setYellow();
        } else {
            setRed();
        }
    }

    public void setPriceItem(ItemStack priceItem) {
        // Ensure the clicked item is not a copy of the divider item stack
        if (priceItem.equals(divider)) {
            return;
        }

        // Replace the current priceItem or emptyPriceItem placeholder with new priceItem
        ItemStack priceItemCopy = priceItem.clone();
        this.priceItem = priceItemCopy;
        priceItemCopy.setAmount(1);
        inventory.setItem(15, priceItemCopy);

        // Reset item amount to zero
        this.priceAmount = 1;

        // Update the confirm button status
        if (!Objects.equals(inventory.getItem(12), emptySellItem) && !Objects.equals(inventory.getItem(15), emptyPriceItem)) {
            // Both slots have valid items
            setYellow();
        } else {
            setRed();
        }
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

        inventory.setItem(12, emptySellItem);

        // Reset item amount to zero
        this.priceAmount = 1;

        // Update the status of the confirm button
        setRed();
    }

    public void resetPriceSlot() {
        // Replace the current price item with the emptyPriceItem placeholder
        TextComponent emptyPriceItemTitle = text("Right click the item in your inventory you want to receive in trade!");
        ItemStack emptyPriceItem = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, emptyPriceItemTitle, null);

        inventory.setItem(15, emptyPriceItem);

        // Reset item amount to zero
        this.priceAmount = 1;

        // Update the status of the confirm button
        setRed();
    }

    public void increasePrice() {
        if (priceAmount < 64 && !Objects.equals(inventory.getItem(15), emptyPriceItem)) {
            priceAmount++;
            Objects.requireNonNull(inventory.getItem(15)).setAmount(priceAmount);

            // Update the confirm button status
            if (!Objects.equals(inventory.getItem(12), emptySellItem) && !Objects.equals(inventory.getItem(15), emptyPriceItem)) {
                // Both slots have valid items
                setYellow();
            } else {
                setRed();
            }
        }
    }

    public void decreasePrice() {
        if (priceAmount > 1 && !Objects.equals(inventory.getItem(15), emptyPriceItem)) {
            priceAmount--;
            Objects.requireNonNull(inventory.getItem(15)).setAmount(priceAmount);

            // Update the confirm button status
            if (!Objects.equals(inventory.getItem(12), emptySellItem) && !Objects.equals(inventory.getItem(15), emptyPriceItem)) {
                // Both slots have valid items
                setYellow();
            } else {
                setRed();
            }
        }
    }

    public ConfirmStatus getConfirmStatus() {
        return this.confirmStatus;
    }

    private void setRed() {
        this.confirmStatus = ConfirmStatus.CANNOT_CONFIRM;
        setButton(0, new Button(cannotConfirmButtonItem, (InventoryClickEvent event) -> {
            // Do nothing
        }));
    }

    private void setYellow() {
        this.confirmStatus = ConfirmStatus.CAN_CONFIRM;
        setButton(0, new Button(canConfirmButtonItem, (InventoryClickEvent event) -> {
            setGreen();
        }));
    }

    private void setGreen() {
        this.confirmStatus = ConfirmStatus.DOUBLE_CONFIRM;
        setButton(0, new Button(doubleConfirmButtonItem, (InventoryClickEvent event) -> {
            ItemStack forSale = inventory.getItem(12);
            ItemStack inReturn = inventory.getItem(15);

            Player player = (Player) event.getWhoClicked();

            // Remove first occurrence of a repeat item stack in the players inventory
            if (player.getInventory().contains(forSale)) {
                player.getInventory().setItem(player.getInventory().first(forSale), null);
            } else {
                WorldShop.getPlugin(WorldShop.class).getLogger().severe("Player attempted to sell an item without it being in their inventory");
                return;
            }

            WorldShop.getStoreManager().createTrade(new Trade(player, forSale, inReturn));

            // Brings the player back to the main page of the store.
            WorldShop.getMenuManager().openMenu(player, new MainShopScreen(player));
        }));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (buttons.containsKey(slot)) {
            event.setCancelled(true);
            buttons.get(slot).handleClick(event);
        } else {
            if (event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                // Determine if it's a right or left click
                if (event.getClick().isLeftClick()) { // Set the item we are selling
                    player.sendMessage("Setting sell item");
                    setSellItem(event.getCurrentItem());
                    event.setCancelled(true);

                } else if (event.getClick().isRightClick()) { // Set the item we want to receive
                    player.sendMessage("Setting price item");
                    setPriceItem(event.getCurrentItem());
                    event.setCancelled(true);
                }
            }
        }
    }
}