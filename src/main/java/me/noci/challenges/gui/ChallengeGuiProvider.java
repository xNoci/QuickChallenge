package me.noci.challenges.gui;

import me.noci.quickutilities.inventory.QuickGUIProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.inventory.InventoryType;


public abstract class ChallengeGuiProvider extends QuickGUIProvider {

    protected ChallengeGuiProvider(int size) {
        super(size);
    }

    protected ChallengeGuiProvider(Component title, int size) {
        super(LegacyComponentSerializer.legacySection().serialize(title), size);
    }

    protected ChallengeGuiProvider(InventoryType type) {
        super(type);
    }

    protected ChallengeGuiProvider(InventoryType type, String title) {
        super(type, title);
    }

}
