package me.noci.challenges.gui;

import me.noci.quickutilities.inventory.PagedQuickGUIProvider;
import me.noci.quickutilities.inventory.QuickGUIProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.inventory.InventoryType;


public abstract class ChallengePagedGuiProvider extends PagedQuickGUIProvider {

    protected ChallengePagedGuiProvider(int size) {
        super(size);
    }

    protected ChallengePagedGuiProvider(Component title, int size) {
        super(LegacyComponentSerializer.legacySection().serialize(title), size);
    }

    protected ChallengePagedGuiProvider(InventoryType type) {
        super(type);
    }

    protected ChallengePagedGuiProvider(InventoryType type, String title) {
        super(type, title);
    }

}
