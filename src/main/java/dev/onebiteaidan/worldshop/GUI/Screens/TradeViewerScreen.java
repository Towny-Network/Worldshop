package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.GUI.GUI;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.GUI.Screen;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.text;

public class TradeViewerScreen extends Screen {

    private final Trade trade;

    public TradeViewerScreen(Player player, Trade trade) {
        setPlayer(player);
        this.trade = trade;

        Component title = text("Trade Viewer")
                .color(NamedTextColor.DARK_GRAY);

        GUI gui = new GUI(27, title, "TradeViewerScreen");
        plugin.getServer().getPluginManager().registerEvents(gui, plugin);

        setGUI(gui);
        initializeScreen();
    }

    @Override
    protected void initializeScreen() {
        // Item Being Sold
        ItemStack beingSold = trade.getItemOffered();
        Button beingSoldButton = new Button(beingSold, () -> {
            player.sendMessage("Clicked item that is being sold!");
        });
        gui.addButton(2, beingSoldButton);

        // Item Being Sold Marker
        String beingSoldMarkerURL = "http://textures.minecraft.net/texture/3e4f2f9698c3f186fe44cc63d2f3c4f9a241223acf0581775d9cecd7075";

        TextComponent beingSoldMarkerTitle = text("You are selling them item!");
        ArrayList<TextComponent> beingSoldLore = new ArrayList<>();
        beingSoldLore.add(text("This will go to the player who buys the item from you."));
        beingSoldLore.add(text("In return, you will receive the payment item(s) you specified."));

        ItemStack beingSoldMarker = Utils.createButtonItem(Utils.createSkull(beingSoldMarkerURL), beingSoldMarkerTitle, beingSoldLore);
        Button beingSoldMarkerButton = new Button(beingSoldMarker, () -> {
            player.sendMessage("Clicked the marker that tells us which item is being sold!");
        });
        gui.addButton(11, beingSoldMarkerButton);

        // Payment Item
        ItemStack paymentItem = trade.getItemRequested();
        Button paymentItemButton = new Button(paymentItem, () -> {
            player.sendMessage("Clicked the payment item!");
        });
        gui.addButton(6, paymentItemButton);

        // Payment Item Marker
        String paymentItemMarkerURL = "http://textures.minecraft.net/texture/3e4f2f9698c3f186fe44cc63d2f3c4f9a241223acf0581775d9cecd7075";

        TextComponent paymentItemMarkerTitle = text("This is the item you requested as payment!");
        ArrayList<TextComponent> paymentItemLore = new ArrayList<>();
        paymentItemLore.add(text("This will go to you after another player buys from you."));
        paymentItemLore.add(text("In return, the buyer will receive the item you are selling."));

        ItemStack paymentItemMarker = Utils.createButtonItem(Utils.createSkull(paymentItemMarkerURL), paymentItemMarkerTitle, paymentItemLore);
        Button paymentItemMarkerButton = new Button(paymentItemMarker, () -> {
            player.sendMessage("Clicked the market that tells us where the payment item is!");
        });
        gui.addButton(15, paymentItemMarkerButton);

        // Back Button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButton = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        Button backButtonButton = new Button(backButton, () -> {

            // Brings the player back to the main page of the store.
            new MainShopScreen(player).openScreen(1);

        });
        gui.addButton(22, backButtonButton);
    }

    @Override
    public void update() {
        //todo: Figure out the behavior here. I think it's supposed to congratulate the seller because you can also delete from this menu iirc.
    }
}
