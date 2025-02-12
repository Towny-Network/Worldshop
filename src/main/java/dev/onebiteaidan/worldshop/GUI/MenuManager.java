package dev.onebiteaidan.worldshop.GUI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;

public class MenuManager implements Listener {
    private final Map<Player, AbstractMenu> openMenus = new HashMap<>();

    public void openMenu(Player player, AbstractMenu menu) {
        openMenus.put(player, menu);
        menu.open(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {

            AbstractMenu menu = openMenus.get(player);
            if (menu != null && event.getClickedInventory() != null) {
                menu.handleClick(event);
            }
        }
    }

//    @EventHandler
//    public void onMenuClose(InventoryCloseEvent event) {
//        if (event.getPlayer() instanceof Player player) {
//            openMenus.remove(player);
//        }
//    }
}
