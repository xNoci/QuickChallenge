package me.noci.challenges.listeners;

import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;

public class AnvilRenameListener implements Listener {

    @EventHandler
    public void onRename(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        AnvilView view = event.getView();

        if (event.getResult() == null) return;

        if (view.getRenameText() == null || view.getRenameText().isBlank()) return;


        boolean freeRename = Option.Settings.Anvil.FREE_RENAME.get();
        if (inventory.getSecondItem() == null && freeRename) {
            view.setRepairCost(0);
        }

        boolean coloredNames = Option.Settings.Anvil.COLORED_NAME.get();
        if (coloredNames) {
            ItemStack itemStack = event.getResult();
            ItemMeta itemMeta = itemStack.getItemMeta();

            String renameText = view.getRenameText();
            itemMeta.displayName(MiniMessage.miniMessage().deserialize(renameText));

            itemStack.setItemMeta(itemMeta);
        }

    }

}
