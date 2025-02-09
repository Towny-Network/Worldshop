package dev.onebiteaidan.worldshop.GUI;

import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import static net.kyori.adventure.text.Component.text;

public class GUIManager {

    private static final Plugin plugin = WorldShop.getPlugin(WorldShop.class);

    public static void openItemBuyerGUI(Player player) {
        Component title = text("Test")
                .color(NamedTextColor.DARK_GRAY);

        GUI gui = new GUI(9, title);
        plugin.getServer().getPluginManager().registerEvents(gui, plugin);

        Button button = new Button(new ItemStack(Material.RED_WOOL), () -> {
            player.sendMessage("FUCKE!");
        });

        gui.addButton(0, button);

        player.openInventory(gui.getInventory());
    }
}
