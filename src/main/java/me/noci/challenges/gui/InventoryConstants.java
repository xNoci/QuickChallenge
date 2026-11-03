package me.noci.challenges.gui;

import me.noci.challenges.settings.Option;
import me.noci.quickutilities.gui.GuiItem;
import me.noci.quickutilities.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryConstants {

    public static ItemStack GLAS_PANE = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).build();

    public static ItemBuilder woodPauseSkull() {
        return ItemBuilder.of(Material.PLAYER_HEAD)
                .skullOwner("c77aae1a26b952493a7371c30ad8c491f12b574cc94a41b2f91a373ca68f9098");
    }

    public static ItemBuilder nextPageItem() {
        return ItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE, Option.Gui.NEXT_PAGE_ITEM.get()).itemFlags();
    }

    public static ItemBuilder previousPageItem() {
        return ItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE, Option.Gui.PREVIOUS_PAGE_ITEM.get()).itemFlags();
    }

    public static ItemBuilder backItem() {
        return ItemBuilder.of(Material.FEATHER, Option.Gui.BACK_ITEM.get()).itemFlags();
    }

}
