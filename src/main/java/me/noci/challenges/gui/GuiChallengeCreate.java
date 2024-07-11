package me.noci.challenges.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.noci.challenges.QuickChallenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.registry.ModifierCreator;
import me.noci.challenges.gui.modifier.GuiModifierOverview;
import me.noci.challenges.gui.modifier.ModifierProvider;
import me.noci.challenges.settings.Config;
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

public class GuiChallengeCreate extends PagedQuickGUIProvider implements ModifierProvider {

    private static final int[] MODIFIER_SLOTS = InventoryPattern.box(3, 3);

    private final ChallengeController challengeController;
    private final List<ModifierCreator> modifiersToAdd = Lists.newArrayList();

    public GuiChallengeCreate(ChallengeController challengeController) {
        super(Option.Gui.ChallengeCreate.TITLE.get(), InventoryConstants.FULL_SIZE);
        this.challengeController = challengeController;
    }

    @Override
    public void init(Player player, InventoryContent content) {
        GuiItem addModifier = new QuickItemStack(Material.ENDER_EYE, Option.Gui.ChallengeCreate.ADD_MODIFIER.get())
                .asGuiItem(event -> {
                    if (event.getClick() != ClickType.LEFT) return;

                    ImmutableList<Class<? extends ChallengeModifier>> modifiersToIgnore = ImmutableList.copyOf(modifiersToAdd.stream().map(ModifierCreator::type).iterator());
                    new GuiModifierOverview(this, modifiersToIgnore).provide(event.getPlayer());
                });

        GuiItem create = new QuickItemStack(Material.ANVIL, Option.Gui.ChallengeCreate.CREATE_MODIFIER.get())
                .asGuiItem(event -> {
                    if (event.getClick() != ClickType.LEFT) return;
                    event.getPlayer().closeInventory();

                    List<ChallengeModifier> modifiers = modifiersToAdd.stream().map(ModifierCreator::create).toList();
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
        if (content.getTotalItemCount() == modifiersToAdd.size()) return;

        GuiItem[] items = modifiersToAdd.stream()
                .map(this::appliedModifier)
                .toArray(GuiItem[]::new);

        content.setPageContent(items);
    }

    @Override
    public void onModifierAdd(ModifierCreator challengeModifier) {
        this.modifiersToAdd.add(challengeModifier);
    }

    private GuiItem appliedModifier(ModifierCreator modifierCreator) {
        Config config = QuickChallenge.instance().config();
        QuickItemStack itemStack = new QuickItemStack(modifierCreator.displayItem())
                .itemLore(
                        Component.empty(),
                        config.get(Option.Gui.ChallengeCreate.MODIFIER_REMOVE_HINT)
                );

        var acceptDialog = GuiAcceptDialog.builder()
                .title(config.get(Option.Gui.ChallengeCreate.RemoveDialog.TITLE))
                .description(
                        Component.empty(),
                        config.get(Option.Gui.ChallengeCreate.RemoveDialog.DESCRIPTION),
                        Component.empty(),
                        config.get(Option.Gui.ChallengeCreate.RemoveDialog.MODIFIER_NAME, Placeholder.unparsed("modifier_name", itemStack.getRawDisplayName()))
                )
                .acceptAction(event -> {
                    this.modifiersToAdd.remove(modifierCreator);
                    this.provide(event.getPlayer());
                })
                .declineAction(event -> this.provide(event.getPlayer()));

        return itemStack.asGuiItem(event -> {
            if (event.getClick() != ClickType.LEFT) return;
            acceptDialog.provide(event.getPlayer());
        });

    }
}
