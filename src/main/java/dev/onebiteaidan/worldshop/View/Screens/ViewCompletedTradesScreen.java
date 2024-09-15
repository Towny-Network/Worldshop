package dev.onebiteaidan.worldshop.View.Screens;

import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.Utils.PageUtils;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners.ViewCompletedTradesScreenListener;
import dev.onebiteaidan.worldshop.View.PageableScreen;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;

public class ViewCompletedTradesScreen extends PageableScreen {

    public ViewCompletedTradesScreen(Player player) {
        setPlayer(player);

        TextComponent title = text("Completed Trades");

        setInventory(plugin.getServer().createInventory(this, 36, title));
        initializeScreen();
        registerListener(new ViewCompletedTradesScreenListener());
    }

    @Override
    protected void initializeScreen() {
        // Back Button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButton = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        getInventory().setItem(31, backButton);


        // Prev Page Button
        TextComponent prevPageTitle = text("Previous Page")
                .color(NamedTextColor.RED);

        if (isPageValid(StoreManager.getInstance().getAllCompletedTradesItems(getPlayer()), getCurrentPage() - 1, 27)) {
            // Add Strikethrough to prevPageTitle if there is no previous page to go to.
            prevPageTitle = prevPageTitle.decorate(TextDecoration.STRIKETHROUGH);
        }

        ItemStack prevPage = Utils.createButtonItem(Material.ARROW, prevPageTitle, null);
        getInventory().setItem(29, prevPage);


        // Next Page Button
        TextComponent nextPageTitle = text("Next Page")
                .color(NamedTextColor.RED);

        if (isPageValid(StoreManager.getInstance().getAllCompletedTradesItems(getPlayer()), getCurrentPage() + 1, 27)) {
            // Add Strikethrough to nextPageTitle if there is no next page to go to.
            nextPageTitle = nextPageTitle.decorate(TextDecoration.STRIKETHROUGH);
        }

        ItemStack nextPage = Utils.createButtonItem(Material.ARROW, nextPageTitle, null);
        getInventory().setItem(33, nextPage);


        // Populate remaining slots w/ completed trades posted by player
        for (ItemStack item: PageUtils.getPageItems(StoreManager.getInstance().getAllCompletedTradesItems(player), getCurrentPage(), 27)) {
            getInventory().addItem(item);
        }
    }

    @Override
    public void openScreen(int page) {
        setCurrentPage(page);
        initializeScreen();
        player.openInventory(getInventory());
    }
}
