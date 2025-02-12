package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.GUI.AbstractMenu;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.Utils.Utils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.text;

public class StoreUpdateScreen extends AbstractMenu {

    public StoreUpdateScreen(Player player) {
        super(text("Oh No!"), 27);
        initializeScreen();
    }

    private void initializeScreen() {
        // Home Menu Button
        TextComponent homeMenuButtonTitle = text("Oh No!");
        ArrayList<TextComponent> lore = new ArrayList<>();
        lore.add(text("The item you were viewing is no longer available!"));
        lore.add(text("Click here to go back to the home screen."));

        ItemStack homeMenuButton = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, homeMenuButtonTitle, lore);
        setButton(13, new Button(homeMenuButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked on the oH NO button");
        }));
    }
}
