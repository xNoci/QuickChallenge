package me.noci.challenges.gui;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.TimerModifier;
import me.noci.challenges.colors.Colors;
import me.noci.quickutilities.inventory.*;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.QuickItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class GuiChallengeOverview extends PagedQuickGUIProvider {

    private static final Component TITLE = Component.text("Challenge Overview", Colors.GUI_TITLE);
    private static final int[] PAGE_CONTENT_SLOTS = InventoryPattern.box(2, 5);

    private final ChallengeController challengeController;

    public GuiChallengeOverview(ChallengeController challengeController) {
        super(TITLE, InventoryConstants.FULL_SIZE);
        this.challengeController = challengeController;
    }

    @Override
    public void init(Player player, InventoryContent content) {
        content.fill(InventoryConstants.GLAS_PANE);
        content.fillSlots(GuiItem.empty(), PAGE_CONTENT_SLOTS);
    }

    @Override
    public void initPage(Player player, PageContent content) {
        content.setItemSlots(PAGE_CONTENT_SLOTS);
        content.setPreviousPageItem(Slot.getSlot(5, 1), InventoryConstants.PREVIOUS_PAGE, InventoryConstants.GLAS_PANE.getItemStack());
        content.setNextPageItem(Slot.getSlot(5, 9), InventoryConstants.NEXT_PAGE, InventoryConstants.GLAS_PANE.getItemStack());


        GuiItem[] challengeItems = challengeController.challenges().stream()
                .sorted(Challenge::compareTo)
                .map(this::toGuiItem)
                .toArray(GuiItem[]::new);

        content.setPageContent(challengeItems);

    }

    @Override
    public void updatePageContent(Player player, PageContent content) {
        for (int i = 0; i < content.getTotalItemCount(); i++) {
            if (!(content.getItem(i) instanceof ChallengeGuiItem guiItem)) continue;
            Challenge challenge = guiItem.challenge();

            content.setItem(i, toGuiItem(challenge));
        }
    }


    private GuiItem toGuiItem(Challenge challenge) {
        TextColor primary = TextColor.color(99, 128, 101);
        TextColor gray = TextColor.color(122, 120, 120);

        TextComponent title = Component.text("Challenge", primary)
                .append(Component.text(" (%s)".formatted(challenge.handle()), gray, TextDecoration.ITALIC));

        var item = new QuickItemStack(InventoryConstants.worldSkull());
        item.displayName(title);

        Set<ChallengeModifier> modifiers = challenge.modifiers();
        int modifierCount = modifiers.size();

        List<Component> lore = Lists.newArrayList(Component.empty());
        lore.add(Component.text("Started: ", gray).append(booleanComponent(challenge.started())));
        lore.add(Component.text("Paused: ", gray).append(booleanComponent(challenge.paused())));
        challenge.modifier(TimerModifier.class)
                .map(TimerModifier::playedTimeAsString)
                .ifPresent(time -> lore.add(Component.text("Time played: ", gray).append(Component.text(time, primary))));

        lore.add(Component.empty());
        lore.add(Component.text("Modifiers ", primary).append(Component.text("(%s)".formatted(modifierCount), gray)).append(Component.text(":", primary)));

        if (modifierCount == 0) {
            lore.add(Component.text("No modifiers applied", NamedTextColor.RED, TextDecoration.ITALIC));
        } else {
            modifiers.forEach(modifier -> lore.add(Component.text("- ", gray).append(Component.text(modifier.name(), primary))));
        }

        lore.add(Component.empty());
        lore.add(Component.text("Beitreten (Linksklick) | Löschen (Rechtsklick)", gray));

        item.lore(lore);

        return new ChallengeGuiItem(challenge, item, event -> {
            switch (event.getClick()) {
                case LEFT -> challenge.join(event.getPlayer());
                case RIGHT -> openDeleteGui(challenge, event.getPlayer(), primary, gray);
            }
        });
    }

    private TextComponent booleanComponent(boolean value) {
        String text = value ? "✔" : "✘";
        TextColor color = value ? NamedTextColor.GREEN : NamedTextColor.RED;
        return Component.text(text, color);
    }

    private void openDeleteGui(Challenge challenge, Player player, TextColor primary, TextColor gray) {
        GuiAcceptDialog.builder()
                .dialogType(GuiAcceptDialog.DialogType.YES_NO)
                .title(
                        Component.text("Challenge löschen?", primary)
                )
                .description(
                        Component.newline(),
                        Component.text("Möchtest du die Challenge wirklich löschen?", gray),
                        Component.newline(),
                        Component.text("Challenge ID: ", gray).append(Component.text(challenge.handle().toString(), primary))
                )
                .acceptAction(event -> {
                    event.getPlayer().closeInventory();
                    challengeController.delete(challenge);
                })
                .closeOnDecline()
                .provide(player);
    }


    @Getter
    private static class ChallengeGuiItem extends GuiItem {

        private final Challenge challenge;

        public ChallengeGuiItem(Challenge challenge, ItemStack itemStack, ClickHandler clickHandler) {
            this.challenge = challenge;
            setItem(itemStack);
            setAction(clickHandler);
        }

    }

}
