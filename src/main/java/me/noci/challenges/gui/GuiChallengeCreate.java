package me.noci.challenges.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.registry.ModifierRegistry;
import me.noci.challenges.gui.modifier.GuiModifierOverview;
import me.noci.challenges.gui.modifier.ModifierApplier;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.inventory.*;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.QuickItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class GuiChallengeCreate extends PagedQuickGUIProvider {

    private static final int[] MODIFIER_SLOTS = InventoryPattern.box(3, 3);

    private final ChallengeController challengeController;
    private final List<ChallengeModifier> modifiers = Lists.newArrayList();

    public GuiChallengeCreate(ChallengeController challengeController) {
        super(Option.Gui.ChallengeCreate.TITLE.get(), InventoryConstants.FULL_SIZE);
        this.challengeController = challengeController;
    }

    @Override
    public void init(Player player, InventoryContent content) {
        GuiItem addModifier = new QuickItemStack(Material.ENDER_EYE, Option.Gui.ChallengeCreate.ADD_MODIFIER.get())
                .asGuiItem(event -> {
                    if (event.getClick() != ClickType.LEFT) return;
                    ImmutableList<Class<? extends ChallengeModifier>> modifiersToIgnore = ImmutableList.copyOf(modifiers.stream().map(ChallengeModifier::getClass).iterator());
                    ModifierApplier applier = modifier -> {
                        if (modifier != null) {
                            modifiers.add(modifier);
                        }
                        provide(player);
                    };
                    new GuiModifierOverview(applier, modifiersToIgnore).provide(event.getPlayer());
                });

        GuiItem create = new QuickItemStack(Material.ANVIL, Option.Gui.ChallengeCreate.CREATE_MODIFIER.get())
                .asGuiItem(event -> {
                    if (event.getClick() != ClickType.LEFT) return;
                    event.getPlayer().closeInventory();
                    challengeController.create(modifiers);
                });

        GuiItem cancel = new QuickItemStack(Material.BARRIER, Option.Gui.ChallengeCreate.CANCEL.get())
                .asGuiItem(event -> {
                    if (event.getClick() != ClickType.LEFT) return;
                    event.getPlayer().closeInventory();
                });

        content.fill(InventoryConstants.GLAS_PANE);
        content.fillSlots(GuiItem.empty(), MODIFIER_SLOTS);
        content.setItem(Slot.getSlot(2, 5), addModifier);
        content.setItem(Slot.getSlot(5, 4), cancel);
        content.setItem(Slot.getSlot(5, 6), create);
    }

    @Override
    public void initPage(Player player, PageContent content) {
        content.setItemSlots(MODIFIER_SLOTS);
        content.setPreviousPageItem(Slot.getSlot(3, 1), InventoryConstants.previousPageItem(), InventoryConstants.GLAS_PANE.getItemStack());
        content.setNextPageItem(Slot.getSlot(3, 9), InventoryConstants.nextPageItem(), InventoryConstants.GLAS_PANE.getItemStack());
    }

    @Override
    public void updatePageContent(Player player, PageContent content) {
        if (content.getTotalItemCount() == modifiers.size()) return;

        GuiItem[] items = modifiers.stream()
                .map(this::appliedModifier)
                .toArray(GuiItem[]::new);

        content.setPageContent(items);
    }

    private GuiItem appliedModifier(ChallengeModifier modifier) {
        QuickItemStack itemStack = ModifierRegistry.displayItem(modifier.getClass())
                .itemLore(
                        Component.empty(),
                        Option.Gui.ChallengeCreate.MODIFIER_REMOVE_HINT.get()
                );

        var acceptDialog = GuiAcceptDialog.builder()
                .title(Option.Gui.ChallengeCreate.RemoveDialog.TITLE.get())
                .description(
                        Component.empty(),
                        Option.Gui.ChallengeCreate.RemoveDialog.DESCRIPTION.get(),
                        Component.empty(),
                        Option.Gui.ChallengeCreate.RemoveDialog.MODIFIER_NAME.resolve(Placeholder.unparsed("modifier_name", itemStack.getRawDisplayName()))
                )
                .acceptAction(event -> {
                    modifiers.remove(modifier);
                    provide(event.getPlayer());
                })
                .declineAction(event -> provide(event.getPlayer()));

        return itemStack.asGuiItem(event -> {
            if (event.getClick() != ClickType.LEFT) return;
            acceptDialog.provide(event.getPlayer());
        });

    }
}
