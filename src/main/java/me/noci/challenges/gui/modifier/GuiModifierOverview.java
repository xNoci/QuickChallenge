package me.noci.challenges.gui.modifier;

import com.google.common.collect.ImmutableList;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.registry.ModifierRegistry;
import me.noci.challenges.colors.Colors;
import me.noci.challenges.gui.InventoryConstants;
import me.noci.quickutilities.inventory.*;
import me.noci.quickutilities.utils.InventoryPattern;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class GuiModifierOverview extends PagedQuickGUIProvider {

    private static final int[] PAGE_SLOTS = InventoryPattern.box(2, 3);

    private final ModifierProvider creatorAddProvider;
    private final ImmutableList<Class<? extends ChallengeModifier>> ignoredModifiers;

    public GuiModifierOverview(ModifierProvider creatorAddProvider, ImmutableList<Class<? extends ChallengeModifier>> ignoredModifiers) {
        super(Component.text("Modifier Hinzufügen", Colors.GUI_TITLE), 9 * 4);
        this.creatorAddProvider = creatorAddProvider;
        this.ignoredModifiers = ignoredModifiers;
    }

    @Override
    public void init(Player player, InventoryContent content) {
        content.fill(InventoryConstants.GLAS_PANE);
        content.fillSlots(GuiItem.empty(), PAGE_SLOTS);

        content.setItem(Slot.getSlot(4, 1), InventoryConstants.openPreviousGui(creatorAddProvider));
    }

    @Override
    public void initPage(Player player, PageContent content) {
        content.setItemSlots(PAGE_SLOTS);
        content.setPreviousPageItem(Slot.getSlot(3, 1), InventoryConstants.PREVIOUS_PAGE, InventoryConstants.GLAS_PANE.getItemStack());
        content.setNextPageItem(Slot.getSlot(3, 1), InventoryConstants.NEXT_PAGE, InventoryConstants.GLAS_PANE.getItemStack());

        GuiItem[] items = ModifierRegistry.modifiers()
                .stream()
                .filter(modifier -> !ignoredModifiers.contains(modifier.type()))
                .map(modifier -> modifier.displayItem().asGuiItem(event -> {
                    if (event.getClick() != ClickType.LEFT) return;
                    modifier.onModifierAdd(creatorAddProvider, event.getPlayer());
                }))
                .toArray(GuiItem[]::new);

        content.setPageContent(items);
    }

}
