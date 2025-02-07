package dev.onebiteaidan.worldshop.GUI;

import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class GUIManager {

    public static void openItemBuyerGUI(Player player) {

        System.out.println("Made it into static function");

        GUI gui = new GUI(Bukkit.createInventory(null, 9, "test"));

        System.out.println("GUI Created");

        JavaPlugin plugin = WorldShop.getPlugin(WorldShop.class);
        plugin.getServer().getPluginManager().registerEvents(gui, plugin);

        System.out.println("Event Registered");

        Button button = new Button(new ItemStack(Material.RED_WOOL), () -> {
           System.out.println("Button Clicked");
        });

        System.out.println("Button Created");

        gui.addButton(0, button);

        System.out.println("Button added to GUI");

        player.openInventory(gui.getInventory());

        System.out.println("Opened Inventory");

    }

}
