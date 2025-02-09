package dev.onebiteaidan.worldshop.GUI;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GUI implements Listener {
    private final Inventory inventory;
    private final Map<Integer, Button> buttons = new HashMap<>();
    private final String id;

    public GUI(int size, Component component, String id) {
        this.inventory = Bukkit.createInventory(null, size, component);
        this.id = id;
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

    public String getID() {
        return id;
    }
}
