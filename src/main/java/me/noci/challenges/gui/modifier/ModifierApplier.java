package me.noci.challenges.gui.modifier;

import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ModifierApplier {

    /**
     * Add challenge modifier to the challenge
     *
     * @param entity   who added the modifier
     * @param modifier modifier that should be added
     */
    void apply(HumanEntity entity, @Nullable ChallengeModifier modifier);

    /**
     * Cancel the modifier application
     */
    void cancel(HumanEntity entity);

}
