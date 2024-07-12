package me.noci.challenges.challenge.modifiers.registry;

import lombok.Getter;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.gui.modifier.ModifierApplier;
import me.noci.quickutilities.utils.Require;
import org.bukkit.entity.Player;

public class Modifier {

    @Getter private final Class<? extends ChallengeModifier> type;
    private final ModifierCreator creator;

    protected Modifier(Class<? extends ChallengeModifier> type, ModifierCreator creator) {
        this.type = type;
        this.creator = creator;
    }

    public void create(Player player, ModifierApplier modifierApplier) {
        Require.nonNull(player);
        Require.nonNull(modifierApplier);
        creator.create(player, modifierApplier);
    }

}
