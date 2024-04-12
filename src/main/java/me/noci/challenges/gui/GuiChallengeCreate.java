package me.noci.challenges.gui;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.noci.challenges.ExitStrategy;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.registry.ModifierCreator;
import me.noci.challenges.colors.Colors;
import me.noci.challenges.gui.modifier.GuiModifierOverview;
import me.noci.challenges.gui.modifier.ModifierProvider;
import me.noci.quickutilities.inventory.*;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.QuickItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class GuiChallengeCreate extends PagedQuickGUIProvider implements ModifierProvider {

    private static final int[] MODIFIER_SLOTS = InventoryPattern.box(3, 3);

    private final ChallengeController challengeController;
    private final List<ModifierCreator> modifiersToAdd = Lists.newArrayList();

    public GuiChallengeCreate(ChallengeController challengeController) {
        super(Component.text("Challenge Create", Colors.GUI_TITLE), InventoryConstants.FULL_SIZE);
        this.challengeController = challengeController;
    }

    @Override
    public void init(Player player, InventoryContent content) {
        GuiItem addModifier = new QuickItemStack(XMaterial.ENDER_EYE.parseMaterial(), Component.text("Modifier hinzufügen", NamedTextColor.GREEN))
                .asGuiItem(event -> {
                    if (event.getClick() != ClickType.LEFT) return;

                    ImmutableList<Class<? extends ChallengeModifier>> modifiersToIgnore = ImmutableList.copyOf(modifiersToAdd.stream().map(ModifierCreator::type).iterator());
                    new GuiModifierOverview(this, modifiersToIgnore).provide(event.getPlayer());
                });

        GuiItem create = new QuickItemStack(XMaterial.ANVIL.parseMaterial(), Component.text("Erstellen", TextColor.color(10, 205, 157)))
                .asGuiItem(event -> {
                    if (event.getClick() != ClickType.LEFT) return;
                    event.getPlayer().closeInventory();

                    List<ChallengeModifier> modifiers = modifiersToAdd.stream().map(ModifierCreator::create).toList();
                    challengeController.create(modifiers, ExitStrategy.SAVE_TO_FILE);
                });

        GuiItem cancel = new QuickItemStack(XMaterial.BARRIER.parseMaterial(), Component.text("Abbrechen", NamedTextColor.RED))
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
        content.setPreviousPageItem(Slot.getSlot(3, 1), InventoryConstants.PREVIOUS_PAGE, InventoryConstants.GLAS_PANE.getItemStack());
        content.setNextPageItem(Slot.getSlot(3, 9), InventoryConstants.NEXT_PAGE, InventoryConstants.GLAS_PANE.getItemStack());
    }

    @Override
    public void updatePageContent(Player player, PageContent content) {
        if (content.getTotalItemCount() == modifiersToAdd.size()) return;

        GuiItem[] items = modifiersToAdd.stream()
                .map(this::fromModifierCreator)
                .toArray(GuiItem[]::new);

        content.setPageContent(items);
    }

    @Override
    public void onModifierAdd(ModifierCreator challengeModifier) {
        this.modifiersToAdd.add(challengeModifier);
    }

    private GuiItem fromModifierCreator(ModifierCreator modifierCreator) {
        TextColor primary = TextColor.color(99, 128, 101);
        TextColor gray = TextColor.color(122, 120, 120);

        QuickItemStack itemStack = new QuickItemStack(modifierCreator.displayItem())
                .lore(
                        Component.empty(),
                        Component.text("Linksklick zum Entfernen", gray)
                );

        var acceptDialog = GuiAcceptDialog.builder()
                .title(
                        Component.text("Modifier entfernen?", primary)
                )
                .description(
                        Component.newline(),
                        Component.text("Möchtest du den Modifier wirklich entfernen?", gray),
                        Component.newline(),
                        Component.text("Modifier Name: ", gray).append(Component.text(itemStack.getRawDisplayName(), primary))
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
