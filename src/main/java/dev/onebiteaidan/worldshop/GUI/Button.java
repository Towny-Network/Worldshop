package dev.onebiteaidan.worldshop.GUI;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Button {
    private final ItemStack item;
    private final ButtonAction action;

    public Button(ItemStack item, ButtonAction action) {
        this.item = item;
        this.action = action;
    }

    public ItemStack getItem() {
        return item;
    }

    public void handleClick(InventoryClickEvent event) {
        if (action != null) {
            action.onClick(event);
        }
    }
}
