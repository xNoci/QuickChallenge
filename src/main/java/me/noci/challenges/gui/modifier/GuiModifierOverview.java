package me.noci.challenges.gui.modifier;

import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.registry.ModifierHolder;
import me.noci.challenges.challenge.modifiers.registry.ModifierRegistry;
import me.noci.challenges.gui.GuiChallengeCreate;
import me.noci.challenges.gui.InventoryConstants;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.gui.Gui;
import me.noci.quickutilities.gui.GuiItem;
import me.noci.quickutilities.gui.PagedGui;
import me.noci.quickutilities.utils.InventoryPattern;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiModifierOverview {

    private static final int[] PAGE_SLOTS = InventoryPattern.box(2, 3);

    private final GuiChallengeCreate guiChallengeCreate;
    private final PagedGui overviewGui;

    public GuiModifierOverview(GuiChallengeCreate guiChallengeCreate) {
        this.guiChallengeCreate = guiChallengeCreate;
        overviewGui = createGui();
    }

    private PagedGui createGui() {
        PagedGui gui = Gui.paged()
                .title(Option.Gui.MODIFIER_OVERVIEW_TITLE.get())
                .rows(4)
                .create();

        gui.fillPattern(InventoryConstants.GLAS_PANE, InventoryPattern.FULL);
        Arrays.stream(PAGE_SLOTS).forEach(gui::removeItem);

        gui.setItem(1, 4,
                InventoryConstants.backItem().asGuiItem(event -> guiChallengeCreate.openGui(event.getWhoClicked())));
        gui.setItem(1, 3, InventoryConstants.previousPageItem().asGuiItem(_ -> gui.previous()));
        gui.setItem(9, 3, InventoryConstants.nextPageItem().asGuiItem(_ -> gui.next()));


        Set<Class<? extends ChallengeModifier>> existingModifiers = guiChallengeCreate.modifiers()
                .stream()
                .map(ChallengeModifier::getClass)
                .collect(Collectors.toSet());

        ModifierApplier applier = new ModifierApplier() {
            @Override
            public void apply(HumanEntity entity, @Nullable ChallengeModifier modifier) {
                guiChallengeCreate.addModifier(modifier);
                guiChallengeCreate.openGui(entity);
            }

            @Override
            public void cancel(HumanEntity entity) {
                guiChallengeCreate.openGui(entity);
            }
        };

        for (ModifierHolder<? extends ChallengeModifier> holder : ModifierRegistry.modifiers()) {
            if (existingModifiers.contains(holder.type())) {
                continue;
            }

            GuiItem item = ModifierRegistry.displayItem(holder.type())
                    .asGuiItem(event -> holder.openCreateGui(event.getWhoClicked(), applier));
            gui.addItem(item);
        }

        return gui;
    }

    public void openGui(HumanEntity player) {
        overviewGui.open(player);
    }

}
