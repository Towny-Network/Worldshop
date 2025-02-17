package dev.onebiteaidan.worldshop.GUI;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMenu {
    protected final Inventory inventory;
    protected final Map<Integer, Button> buttons = new HashMap<>();

    public AbstractMenu(Component title, int size) {
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public void setButton(int slot, Button button) {
        inventory.setItem(slot, button.getItem());
        buttons.put(slot, button);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (buttons.containsKey(slot)) {
            event.setCancelled(true);
            buttons.get(slot).handleClick(event);
        }
    }
}
