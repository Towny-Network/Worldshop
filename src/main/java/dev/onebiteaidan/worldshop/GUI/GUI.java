package dev.onebiteaidan.worldshop.GUI;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GUI implements Listener {
    private final Inventory inventory;
    private final Map<Integer, Button> buttons = new HashMap<>();

    public GUI(Inventory inventory) {
        this.inventory = inventory;
    }

    public void addButton(int slot, Button button) {
        inventory.setItem(slot, button.getItem());
        buttons.put(slot, button);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory() != inventory) return;

        Button button = buttons.get(event.getSlot());
        if (button != null) {
            event.setCancelled(true);
            button.runAction(event);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
