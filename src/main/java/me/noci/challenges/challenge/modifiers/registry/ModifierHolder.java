package me.noci.challenges.challenge.modifiers.registry;

import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.gui.modifier.ModifierApplier;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.HumanEntity;

public record ModifierHolder<T extends ChallengeModifier>(Key key, Class<T> type, ModifierCreator creator) {

    public void openCreateGui(HumanEntity player, ModifierApplier applier) {
        creator.open(player, applier);
    }

}
