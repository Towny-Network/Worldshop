package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.GUI.AbstractMenu;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.text;

public class TradeViewerScreen extends AbstractMenu {

    private final Trade trade;

    public TradeViewerScreen(Trade trade) {
        super(text("Trade Viewer").color(NamedTextColor.DARK_GRAY), 27);

        this.trade = trade;
        initializeScreen();
    }

    private void initializeScreen() {
        // Item Being Sold
        ItemStack beingSold = trade.getItemOffered();
        setButton(2, new Button(beingSold, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
        }));

        // Item Being Sold Marker
        String beingSoldMarkerURL = "http://textures.minecraft.net/texture/3e4f2f9698c3f186fe44cc63d2f3c4f9a241223acf0581775d9cecd7075";

        TextComponent beingSoldMarkerTitle = text("You are selling them item!");
        ArrayList<TextComponent> beingSoldLore = new ArrayList<>();
        beingSoldLore.add(text("This will go to the player who buys the item from you."));
        beingSoldLore.add(text("In return, you will receive the payment item(s) you specified."));

        ItemStack beingSoldMarker = Utils.createButtonItem(Utils.createSkull(beingSoldMarkerURL), beingSoldMarkerTitle, beingSoldLore);
        setButton(11, new Button(beingSoldMarker, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
        }));

        // Payment Item
        ItemStack paymentItem = trade.getItemRequested();
        setButton(6, new Button(paymentItem, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
        }));

        // Payment Item Marker
        String paymentItemMarkerURL = "http://textures.minecraft.net/texture/3e4f2f9698c3f186fe44cc63d2f3c4f9a241223acf0581775d9cecd7075";

        TextComponent paymentItemMarkerTitle = text("This is the item you requested as payment!");
        ArrayList<TextComponent> paymentItemLore = new ArrayList<>();
        paymentItemLore.add(text("This will go to you after another player buys from you."));
        paymentItemLore.add(text("In return, the buyer will receive the item you are selling."));

        ItemStack paymentItemMarker = Utils.createButtonItem(Utils.createSkull(paymentItemMarkerURL), paymentItemMarkerTitle, paymentItemLore);
        setButton(15, new Button(paymentItemMarker, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
        }));

        // Back Button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButton = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        setButton(22, new Button(backButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            WorldShop.getMenuManager().openMenu(player, new MainShopScreen(player));
        }));
    }

    public Trade getTrade() {
        return this.trade;
    }
}
