package me.noci.challenges.challenge.modifiers.registry;

import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.gui.modifier.ModifierApplier;
import org.bukkit.entity.HumanEntity;

@FunctionalInterface
public interface ModifierCreator {

    /**
     * This method is used to open the gui to configure a {@link ChallengeModifier}.
     * The ui than will interact with the {@link ModifierApplier} to apply the modifier.
     *
     * @param player          that will configure the modifier
     * @param modifierApplier that will apply the modifier
     */
    void open(HumanEntity player, ModifierApplier modifierApplier);

}