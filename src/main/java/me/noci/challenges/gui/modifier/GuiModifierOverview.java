package me.noci.challenges.gui.modifier;

import com.google.common.collect.ImmutableList;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.registry.ModifierRegistry;
import me.noci.challenges.gui.InventoryConstants;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.inventory.*;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.QuickItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class GuiModifierOverview extends PagedQuickGUIProvider {

    private static final int[] PAGE_SLOTS = InventoryPattern.box(2, 3);

    private final ModifierApplier modifierApplier;
    private final ImmutableList<Class<? extends ChallengeModifier>> ignoredModifiers;

    public GuiModifierOverview(ModifierApplier modifierApplier, ImmutableList<Class<? extends ChallengeModifier>> ignoredModifiers) {
        super(Option.Gui.MODIFIER_OVERVIEW_TITLE.get(), 36);
        this.modifierApplier = modifierApplier;
        this.ignoredModifiers = ignoredModifiers;
    }

    @Override
    public void init(Player player, InventoryContent content) {
        content.fill(InventoryConstants.GLAS_PANE);
        content.fillSlots(GuiItem.empty(), PAGE_SLOTS);

        content.setItem(Slot.getSlot(4, 1), InventoryConstants.backItem().asGuiItem(event -> modifierApplier.cancel()));
    }

    @Override
    public void initPage(Player player, PageContent content) {
        content.setItemSlots(PAGE_SLOTS);
        content.setPreviousPageItem(Slot.getSlot(3, 1), InventoryConstants.previousPageItem(), InventoryConstants.GLAS_PANE.getItemStack());
        content.setNextPageItem(Slot.getSlot(3, 1), InventoryConstants.nextPageItem(), InventoryConstants.GLAS_PANE.getItemStack());

        GuiItem[] items = ModifierRegistry.modifiers()
                .filter(modifier -> !ignoredModifiers.contains(modifier.type()))
                .map(modifier -> {
                    QuickItemStack item = ModifierRegistry.displayItem(modifier.type());

                    return item.asGuiItem(event -> {
                        if (event.getClick() != ClickType.LEFT) return;
                        modifier.create(player, modifierApplier);
                    });
                })
                .toArray(GuiItem[]::new);

        content.setPageContent(items);
    }

}
