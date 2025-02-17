package dev.onebiteaidan.worldshop.GUI;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface ButtonAction {
    void onClick(InventoryClickEvent event);
}
