package me.noci.challenges.gui.modifier;

import me.noci.challenges.challenge.modifiers.registry.ModifierCreator;
import me.noci.quickutilities.inventory.GuiProvider;

public interface ModifierProvider extends GuiProvider {

    void onModifierAdd(ModifierCreator challengeModifier);

}
