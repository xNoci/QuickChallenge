package me.noci.challenges.gui;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
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
    public static final QuickItemStack PREVIOUS_PAGE = new QuickItemStack(XMaterial.ARROW.parseMaterial(), "§8§l◀ §r§7Vorherige Seite" /*TODO USE COMPONENT*/).addItemFlags();
    public static final QuickItemStack NEXT_PAGE = new QuickItemStack(XMaterial.ARROW.parseMaterial(), "§r§7Nächste Seite §8§l▶" /*TODO USE COMPONENT*/).addItemFlags();
    public static final QuickItemStack PREVIOUS_GUI = new QuickItemStack(XMaterial.FEATHER.parseMaterial(), "Zurück"/*TODO USE COMPONENT*/).addItemFlags();

    public static GuiItem openPreviousGui(GuiProvider gui) {
        return PREVIOUS_GUI.asGuiItem(event -> gui.provide(event.getPlayer()));
    }

    public static ItemStack worldSkull() {
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        Objects.requireNonNull(item, "Item cannot be null");
        ItemMeta itemMeta = SkullUtils.applySkin(item.getItemMeta(), "f151cffdaf303673531a7651b36637cad912ba485643158e548d59b2ead5011");
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack woodPauseSkull() {
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        Objects.requireNonNull(item, "Item cannot be null");
        ItemMeta itemMeta = SkullUtils.applySkin(item.getItemMeta(), "c77aae1a26b952493a7371c30ad8c491f12b574cc94a41b2f91a373ca68f9098");
        item.setItemMeta(itemMeta);
        return item;
    }

}
