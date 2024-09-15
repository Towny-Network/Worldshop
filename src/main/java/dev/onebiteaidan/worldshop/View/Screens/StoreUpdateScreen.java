package dev.onebiteaidan.worldshop.View.Screens;

import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners.StoreUpdateScreenListener;
import dev.onebiteaidan.worldshop.View.Screen;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.text;

public class StoreUpdateScreen extends Screen {

    public StoreUpdateScreen(Player player) {
        setPlayer(player);

        TextComponent title = text("Oh No!");

        setInventory(plugin.getServer().createInventory(this, 27, title));
        initializeScreen();
        registerListener(new StoreUpdateScreenListener());
    }

    @Override
    protected void initializeScreen() {
        // Home Menu Button
        TextComponent homeMenuButtonTitle = text("Oh No!");
        ArrayList<TextComponent> lore = new ArrayList<>();
        lore.add(text("The item you were viewing is no longer available!"));
        lore.add(text("Click here to go back to the home screen."));

        ItemStack homeMenuButton = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, homeMenuButtonTitle, lore);
        getInventory().setItem(13, homeMenuButton);
    }

    @Override
    public void update() {
        // Do nothing
    }
}
