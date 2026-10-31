package me.noci.challenges.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.inventory.GuiItem;
import me.noci.quickutilities.inventory.GuiProvider;
import me.noci.quickutilities.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;
import java.util.UUID;

public class InventoryConstants {

    public static final int FULL_SIZE = 54;
    public static GuiItem GLAS_PANE = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).asGuiItem();

    public static GuiItem openPreviousGui(GuiProvider gui) {
        return backItem().asGuiItem(event -> gui.provide(event.getPlayer()));
    }

    public static ItemBuilder woodPauseSkull() {
        ItemBuilder item = ItemBuilder.of(Material.PLAYER_HEAD);
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        String textureUrl = "http://textures.minecraft.net/texture/c77aae1a26b952493a7371c30ad8c491f12b574cc94a41b2f91a373ca68f9098";
        String jsonJson = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", textureUrl);
        String encodedBase64 = Base64.getEncoder().encodeToString(jsonJson.getBytes());
        profile.setProperty(new ProfileProperty("textures", encodedBase64));
        item.withSkullOwner(profile);
        return item;
    }

    public static ItemStack nextPageItem() {
        return ItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE, Option.Gui.NEXT_PAGE_ITEM.get()).withItemFlags().build();
    }

    public static ItemStack previousPageItem() {
        return ItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE, Option.Gui.PREVIOUS_PAGE_ITEM.get()).withItemFlags().build();
    }

    public static ItemBuilder backItem() {
        return ItemBuilder.of(Material.FEATHER, Option.Gui.BACK_ITEM.get()).withItemFlags();
    }

}
