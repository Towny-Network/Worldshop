package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.GUI.Screen;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.text;

public class TradeManagementScreen extends Screen {

    public TradeManagementScreen(Player player) {
        setPlayer(player);

        TextComponent title = text("Trades");

        setInventory(plugin.getServer().createInventory(this, 27, title));
        initializeScreen();
    }

    @Override
    protected void initializeScreen() {
        // View Current Listings Button
        TextComponent currentListingsButtonTitle = text("Current Listings")
                .color(NamedTextColor.GREEN);
        ArrayList<TextComponent> currentListingsButtonLore = new ArrayList<>();
        currentListingsButtonLore.add(text("Click to view your current listings!"));

        ItemStack currentListingsButton = Utils.createButtonItem(Material.CHEST, currentListingsButtonTitle, currentListingsButtonLore);
        getInventory().setItem(11, currentListingsButton);


        // View Completed Trades Button
        TextComponent completedTradesButtonTitle = text("Completed Trades")
                .color(NamedTextColor.YELLOW);
        ArrayList<TextComponent> completedListingsButtonLore = new ArrayList<>();
        completedListingsButtonLore.add(text("Click to view your recently completed trades!"));

        ItemStack completedTradesButton = Utils.createButtonItem(Material.BARREL, completedTradesButtonTitle, completedListingsButtonLore);
        getInventory().setItem(15, completedTradesButton);


        // Back Button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButton = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        getInventory().setItem(22, backButton);
    }

    @Override
    public void update() {
        // Do nothing
    }
}
