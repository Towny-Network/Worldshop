package dev.onebiteaidan.worldshop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class StoreManager {

    private class Trade {
        ItemStack forSale;
        ItemStack wanted;
        ItemStack displayItem;

        private Trade(ItemStack forSale, ItemStack wanted) {
            
        }
    }

    ArrayList<Trade> trades;

    public StoreManager() {
        // Grabs all of the trades from the database

    }





    public void purchase(Trade trade, Player player) {
        // Todo: Build a gui for inputting item + amount wanted for trade
        // Build a gui that has two parts. A part for entering the name via a sign
        // A part where you can set the number of items using like (-5, -1, +1, +5)
    }

}
