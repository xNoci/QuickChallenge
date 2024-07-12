package me.noci.challenges.gui.modifier;

import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.quickutilities.inventory.GuiProvider;
import me.noci.quickutilities.inventory.QuickGUIProvider;
import net.kyori.adventure.text.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ModifierCreateGui<T extends ChallengeModifier> extends QuickGUIProvider {

    protected final ModifierApplier modifierApplier;

    protected ModifierCreateGui(ModifierApplier modifierApplier, Component title, int size) {
        super(title, size);
        this.modifierApplier = modifierApplier;
    }

}
