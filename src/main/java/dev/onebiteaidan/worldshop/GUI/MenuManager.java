package dev.onebiteaidan.worldshop.GUI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

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
            if (menu == null || event.getClickedInventory() == null) {
                return; // Ignore clicks if no menu is open
            }

            //fixme: There may be a memory leak here. Below is a redundant check. In theory, the player should be removed from all listeners when they dont have a menu open.

            if (menu.inventory.equals(event.getClickedInventory())) {
                menu.handleClick(event);
            }
        }
    }
}
