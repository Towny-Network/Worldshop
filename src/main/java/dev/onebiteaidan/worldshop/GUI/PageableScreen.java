package dev.onebiteaidan.worldshop.GUI;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class PageableScreen extends Screen {

    protected int currentPage;

    /**
     * Open screen on specific page.
     * @param page to open screen on.
     */
    public abstract void openScreen(int page);

    /**
     * Open screen on next page.
     * Does not do its own page validation.
     */
    public void nextPage() {
        openScreen(currentPage + 1);
        currentPage += 1;
    }

    /**
     * Open screen on previous page.
     * Does not do its own page validation.
     */
    public void previousPage() {
        openScreen(currentPage - 1);
        currentPage -= 1;
    }

    /**
     * Set the current page.
     * @param currentPage to update the current page to.
     */
    protected void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * Get the current page.
     * @return current page.
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Get the items that would be displayed on the screen.
     * @param items list that is segmented into pages.
     * @param page number of the page you want items for.
     * @param spaces to allocate for the items (Starting from 0, and filling in page until spaces bound hit or out of items.)
     * @return items that will be displayed on the page.
     */
    public static <T extends ItemStack> List<T> getPageItems(List<T> items, int page, int spaces) {
        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;

        List<T> newItems = new ArrayList<>();
        for (int i = lowerBound; i < upperBound; i++) {
            try {
                newItems.add(items.get(i));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        return newItems;
    }

    /**
     * Checks if page is valid at a page number.
     * @param items list that would be segmented into pages.
     * @param page number of page you want to check.
     * @param spaces to allocate for the items (Starting from 0, and filling in page until spaces bound hit or out of items.)
     * @return whether page is valid.
     */
    public static <T extends ItemStack> boolean isPageValid(List<T> items, int page, int spaces) {
        if (page <= 0) {
            return false;
        }

        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;

        return items.size() > lowerBound;
    }
}
