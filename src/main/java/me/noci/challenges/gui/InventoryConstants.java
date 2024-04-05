package me.noci.challenges.gui;

import me.noci.quickutilities.inventory.GuiItem;
import me.noci.quickutilities.utils.QuickItemStack;
import org.bukkit.Material;

public class InventoryConstants {

    public static final int FULL_SIZE = 54;
    public static GuiItem GLAS_PANE = new QuickItemStack(Material.BLACK_STAINED_GLASS_PANE).asGuiItem();
    public static final QuickItemStack PREVIOUS_PAGE = new QuickItemStack(Material.ARROW, "§8§l◀ §r§7Previous page" /*TODO USE COMPONENT*/).addItemFlags();
    public static final QuickItemStack NEXT_PAGE = new QuickItemStack(Material.ARROW, "§r§7Next page §8§l▶" /*TODO USE COMPONENT*/).addItemFlags();

}
