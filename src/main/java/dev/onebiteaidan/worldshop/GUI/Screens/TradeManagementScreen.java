package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.GUI.AbstractMenu;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.Utils.Utils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.text;

public class TradeManagementScreen extends AbstractMenu {

    public TradeManagementScreen() {
        super(text("Trades"), 27);
        initializeScreen();
    }

    public void initializeScreen() {
        // View Current Listings Button
        TextComponent currentListingsButtonTitle = text("Current Listings")
                .color(NamedTextColor.GREEN);
        ArrayList<TextComponent> currentListingsButtonLore = new ArrayList<>();
        currentListingsButtonLore.add(text("Click to view your current listings!"));

        ItemStack currentListingsButton = Utils.createButtonItem(Material.CHEST, currentListingsButtonTitle, currentListingsButtonLore);
        setButton(11, new Button(currentListingsButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked current listings button");
        }));


        // View Completed Trades Button
        TextComponent completedTradesButtonTitle = text("Completed Trades")
                .color(NamedTextColor.YELLOW);
        ArrayList<TextComponent> completedListingsButtonLore = new ArrayList<>();
        completedListingsButtonLore.add(text("Click to view your recently completed trades!"));

        ItemStack completedTradesButton = Utils.createButtonItem(Material.BARREL, completedTradesButtonTitle, completedListingsButtonLore);
        setButton(15, new Button(completedTradesButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked completed button");
        }));


        // Back Button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButton = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        setButton(22, new Button(backButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked back button button");
        }));
    }
}
