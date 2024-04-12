package me.noci.challenges.gui.modifier;

import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.quickutilities.inventory.GuiProvider;
import me.noci.quickutilities.inventory.QuickGUIProvider;
import net.kyori.adventure.text.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ModifierCreateGui<T extends ChallengeModifier> extends QuickGUIProvider {

    protected final GuiProvider parentGui;
    protected final Consumer<Supplier<T>> onModifierCreate;

    protected ModifierCreateGui(GuiProvider parentGui, Consumer<Supplier<T>> onModifierCreate, Component title, int size) {
        super(title, size);
        this.parentGui = parentGui;
        this.onModifierCreate = onModifierCreate;
    }

}
