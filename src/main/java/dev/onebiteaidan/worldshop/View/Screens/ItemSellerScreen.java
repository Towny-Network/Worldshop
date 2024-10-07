package dev.onebiteaidan.worldshop.View.Screens;

import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners.ItemSellerScreenListener;
import dev.onebiteaidan.worldshop.View.Screen;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;


public class ItemSellerScreen extends Screen {

    public enum ConfirmStatus {
        CANNOT_CONFIRM,     // Red
        CAN_CONFIRM,        // Yellow
        DOUBLE_CONFIRM      // Green
    }

    // Only item stacks that are used multiple times in are declared up here
    ItemStack cannotConfirmButton;
    ItemStack canConfirmButton;
    ItemStack doubleConfirmButton;
    ItemStack emptySellItem; // Placeholder for empty sellItem
    ItemStack emptyPriceItem; // Placeholder for empty priceItem
    ItemStack divider;

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

        setInventory(WorldShop.getPlugin(WorldShop.class).getServer().createInventory(this, 27, title)); //Todo: make the title of the store change based on nation it's in
        registerListener(new ItemSellerScreenListener());

        // Initialize all non-standard Screen item stacks
        initializeConfirmButtons();
        initializeEmptyPlaceholders();

        // Initialize screen
        initializeScreen();
    }

    @Override
    protected void initializeScreen() {
        // Dividers
        this.divider = Utils.createButtonItem(Material.GRAY_STAINED_GLASS_PANE, null, null);
        getInventory().setItem(1, divider);
        getInventory().setItem(10, divider);
        getInventory().setItem(19, divider);


        // Confirm the trade button (cannot confirm is the default state)
        getInventory().setItem(0, cannotConfirmButton);


        // Item player wants to sell
        getInventory().setItem(12, emptySellItem);


        // Increase the number of items the player wants in return
        String increasePriceButtonURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19";
        TextComponent increasePriceButtonTitle = text("Increase Price by 1");
        ItemStack increasePriceButton = Utils.createButtonItem(Utils.createSkull(increasePriceButtonURL), increasePriceButtonTitle, null);
        getInventory().setItem(14, increasePriceButton);


        // Item player wants to receive
        getInventory().setItem(15, emptyPriceItem);


        // Decrease the number of items the player wants in return
        String decreasePriceButtonURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=";
        TextComponent decreasePriceButtonTitle = text("Decrease Price by 1");
        ItemStack decreasePriceButton = Utils.createButtonItem(Utils.createSkull(decreasePriceButtonURL), decreasePriceButtonTitle, null);
        getInventory().setItem(16, decreasePriceButton);


        // Go back to the main menu button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButton = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        getInventory().setItem(18, backButton);
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
        this.priceAmount = 0;

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
        getInventory().setItem(15, priceItem);

        // Reset item amount to zero
        this.priceAmount = 0;

        // Update the confirm button status
        updateStatus();
    }

    public void resetSellSlot() {
        // Replace the current sell item with the emptySellItem placeholder
        // ItemStacks that represent no item present
        TextComponent emptySellItemTitle = text("Left click the item in your inventory you want to sell!");
        ItemStack emptySellItem = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, emptySellItemTitle, null);

        this.getInventory().setItem(12, emptySellItem);

        // Reset item amount to zero
        this.priceAmount = 0;

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
        this.priceAmount = 0;

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
                this.getInventory().setItem(0, canConfirmButton);

            } else if (confirmStatus == ConfirmStatus.CAN_CONFIRM) {
                confirmStatus = ConfirmStatus.DOUBLE_CONFIRM; // Goes to Green Checkmark state
                this.getInventory().setItem(0, doubleConfirmButton);
            }

        } else {
            // Go to Red X state
            confirmStatus = ConfirmStatus.CANNOT_CONFIRM;
            this.getInventory().setItem(0, cannotConfirmButton);
        }
    }

    private void initializeConfirmButtons() {
        // Define RED X
        String cannotConfirmURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZDlkZTYyZWNhZTliNzk4NTU1ZmQyM2U4Y2EzNWUyNjA1MjkxOTM5YzE4NjJmZTc5MDY2Njk4Yzk1MDhhNyJ9fX0=";
        TextComponent cannotConfirmTitle = text("You cannot confirm until you have put in a sell item and a price item!");
        this.cannotConfirmButton = Utils.createButtonItem(Utils.createSkull(cannotConfirmURL), cannotConfirmTitle, null);

        // Define the YELLOW Checkmark
        String canConfirmURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmNDI1YjRkYjdkNjJiMjAwZTg5YzAxM2U0MjFhOWUxMTBiZmIyN2YyZDhiOWY1ODg0ZDEwMTA0ZDAwZjRmNCJ9fX0=";
        TextComponent canConfirmTitle = text("Click to confirm!");
        this.canConfirmButton = Utils.createButtonItem(Utils.createSkull(canConfirmURL), canConfirmTitle, null);

        // Define the GREEN Checkmark
        String doubleConfirmURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";
        TextComponent doubleConfirmTitle = text("Are you sure?");
        this.doubleConfirmButton = Utils.createButtonItem(Utils.createSkull(doubleConfirmURL), doubleConfirmTitle, null);
    }

    private void initializeEmptyPlaceholders() {
        // Define emptySellItem placeholder
        TextComponent emptySellItemTitle = text("Left click the item in your inventory you want to sell!");
        this.emptySellItem = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, emptySellItemTitle, null);

        // Define emptyPriceItem placeholder
        TextComponent emptyPriceItemTitle = text("Right click the item in your inventory you want to receive in trade!");
        this.emptyPriceItem = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, emptyPriceItemTitle, null);
    }
}