package me.noci.challenges.challenge.modifiers.registry;

import me.noci.challenges.gui.modifier.ModifierApplier;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface ModifierCreator {

    void create(Player player, ModifierApplier modifierApplier);

}
