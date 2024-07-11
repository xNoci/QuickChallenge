package me.noci.challenges.gui;

import com.cryptomorin.xseries.SkullUtils;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.inventory.GuiItem;
import me.noci.quickutilities.inventory.GuiProvider;
import me.noci.quickutilities.utils.QuickItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class InventoryConstants {

    public static final int FULL_SIZE = 54;
    public static GuiItem GLAS_PANE = new QuickItemStack(Material.BLACK_STAINED_GLASS_PANE).asGuiItem();

    public static GuiItem openPreviousGui(GuiProvider gui) {
        return backItem().asGuiItem(event -> gui.provide(event.getPlayer()));
    }

    public static QuickItemStack woodPauseSkull() {
        QuickItemStack item = new QuickItemStack(Material.PLAYER_HEAD);
        Objects.requireNonNull(item, "Item cannot be null");
        ItemMeta itemMeta = SkullUtils.applySkin(item.getItemMeta(), "c77aae1a26b952493a7371c30ad8c491f12b574cc94a41b2f91a373ca68f9098");
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack nextPageItem() {
        return new QuickItemStack(Material.GREEN_STAINED_GLASS_PANE, Option.Gui.NEXT_PAGE_ITEM.get()).addItemFlags();
    }

    public static ItemStack previousPageItem() {
        return new QuickItemStack(Material.GREEN_STAINED_GLASS_PANE, Option.Gui.PREVIOUS_PAGE_ITEM.get()).addItemFlags();
    }

    public static QuickItemStack backItem() {
        return new QuickItemStack(Material.FEATHER, Option.Gui.BACK_ITEM.get()).addItemFlags();
    }

}
