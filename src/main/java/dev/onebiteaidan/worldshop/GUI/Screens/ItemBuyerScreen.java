package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.GUI.AbstractMenu;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static dev.onebiteaidan.worldshop.GUI.StaticItems.doubleConfirmButtonItem;
import static net.kyori.adventure.text.Component.text;

public class ItemBuyerScreen extends AbstractMenu {

    private final Trade trade;

    public ItemBuyerScreen(Trade trade) {
        super(text("Item Buyer").color(NamedTextColor.DARK_GRAY), 9);

        this.trade = trade;
        initializeScreen();
    }

    private void initializeScreen() {
        // Back Button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButton = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        setButton(0, new Button(backButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            WorldShop.getMenuManager().openMenu(player, new MainShopScreen(player));
        }));

        // Confirm Button
        ItemStack confirmButton = doubleConfirmButtonItem;
        setButton(2, new Button(confirmButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked the confirm button");
            player.closeInventory();
        }));


        // Item You're Buying
        ItemStack beingSold = trade.getItemOffered();
        setButton(4, new Button(beingSold, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked item that is being sold!");
        }));


        // Buying Marker
        ItemStack buyingMarker = new ItemStack(Material.OAK_PLANKS);
        setButton(5, new Button(beingSold, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicking on the buying marker");
        }));


        // Selling Marker
        ItemStack sellingMarker = new ItemStack(Material.OAK_PLANKS);
        setButton(6, new Button(beingSold, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicking on the selling marker");
        }));


        // Payment Item
        ItemStack paymentItem = trade.getItemRequested();
        setButton(7, new Button(paymentItem, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked the payment item!");
        }));


    }

    public Trade getTrade() {
        return this.trade;
    }
}
