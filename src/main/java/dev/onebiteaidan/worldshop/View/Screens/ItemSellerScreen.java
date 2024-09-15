package dev.onebiteaidan.worldshop.View.Screens;

import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners.ItemSellerScreenListener;
import dev.onebiteaidan.worldshop.View.Screen;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;

public class ItemSellerScreen extends Screen {

    public ItemSellerScreen(Player player) {
        setPlayer(player);

        Component title = text("What would you like to sell?")
                .color(NamedTextColor.DARK_GRAY);

        setInventory(WorldShop.getPlugin(WorldShop.class).getServer().createInventory(this, 27, title)); //Todo: make the title of the store change based on nation it's in
        initializeScreen();
        registerListener(new ItemSellerScreenListener());
    }

    @Override
    protected void initializeScreen() {
        // Dividers
        ItemStack divider = Utils.createButtonItem(Material.GRAY_STAINED_GLASS_PANE, null, null);
        getInventory().setItem(1, divider);
        getInventory().setItem(10, divider);
        getInventory().setItem(19, divider);


        // Confirm the trade button
        String confirmTradeButtonURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZDlkZTYyZWNhZTliNzk4NTU1ZmQyM2U4Y2EzNWUyNjA1MjkxOTM5YzE4NjJmZTc5MDY2Njk4Yzk1MDhhNyJ9fX0=";

        TextComponent confirmTradeButtonTitle = text("You cannot confirm until you have put in a sell item and a price item!");

        ItemStack confirmTradeButton = Utils.createButtonItem(Utils.createSkull(confirmTradeButtonURL), confirmTradeButtonTitle, null);
        getInventory().setItem(0, confirmTradeButton);


        // Item player wants to sell
        TextComponent blankItemSpotButtonTitle = text("Press ")
                .append(Component.keybind().keybind("key.break"))
                .append(text(" the item in your inventory you want to sell!"));

        ItemStack blankItemSpotButton = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, blankItemSpotButtonTitle, null);
        getInventory().setItem(12, blankItemSpotButton);


        // Increase the number of items the player wants in return
        String increasePriceButtonURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19";

        TextComponent increasePriceButtonTitle = text("Increase Price by 1");

        ItemStack increasePriceButton = Utils.createButtonItem(Utils.createSkull(increasePriceButtonURL), increasePriceButtonTitle, null);
        getInventory().setItem(14, increasePriceButton);


        // Item player wants to receive
        TextComponent priceButtonTitle = text("Press ")
                .append(Component.keybind().keybind("key.place"))
                .append(text(" the item in your inventory you want to receive in trade!"));

        ItemStack priceButton = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, priceButtonTitle, null);
        getInventory().setItem(15, priceButton);


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
}
