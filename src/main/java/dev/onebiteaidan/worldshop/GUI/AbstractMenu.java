package dev.onebiteaidan.worldshop.GUI;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
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
        } else {
            // Where was the click performed?
            if (event.getClickedInventory().equals(event.getView().getTopInventory())) {
                // Top inventory
                event.setCancelled(true);
            } else {
                // Bottom inventory
                if (event.getClick().isShiftClick()) {
                    event.setCancelled(true);
                }
                if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
