package me.noci.challenges.gui;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Lists;
import lombok.Getter;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.TimerModifier;
import me.noci.quickutilities.inventory.*;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.QuickItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GuiChallengeOverview extends ChallengePagedGuiProvider {

    private static final ItemStack SKULL_ITEM = skullItem();
    private static final Component TITLE = Component.text("", TextColor.color(132, 120, 157));
    private static final int[] PAGE_CONTENT_SLOTS = InventoryPattern.box(2, 5);

    private final List<Challenge> challenges;

    public GuiChallengeOverview(List<Challenge> challenges) {
        super(TITLE, InventoryConstants.FULL_SIZE);
        this.challenges = challenges;
    }

    @Override
    public void init(Player player, InventoryContent content) {
        content.fill(InventoryConstants.GLAS_PANE);
        content.fillSlots(GuiItem.empty(), PAGE_CONTENT_SLOTS);
    }

    @Override
    public void initPage(Player player, PageContent content) {
        content.setItemSlots(PAGE_CONTENT_SLOTS);
        content.setPreviousPageItem(Slot.getSlot(6, 1), InventoryConstants.PREVIOUS_PAGE, InventoryConstants.GLAS_PANE.getItemStack());
        content.setPreviousPageItem(Slot.getSlot(6, 1), InventoryConstants.PREVIOUS_PAGE, InventoryConstants.GLAS_PANE.getItemStack());


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
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        TextColor primary = TextColor.color(99, 128, 101);
        TextColor gray = TextColor.color(122, 120, 120);

        TextComponent title = Component.text("Challenge", primary)
                .append(Component.text(" (%s)".formatted(challenge.handle()), gray, TextDecoration.ITALIC));

        var item = new QuickItemStack(SKULL_ITEM);
        item.setDisplayName(serializer.serialize(title));

        Set<ChallengeModifier> modifiers = challenge.modifiers();
        int modifierCount = modifiers.size();

        List<String> lore = Lists.newArrayList(" ");
        lore.add(serializer.serialize(Component.text("Started: ", gray).append(booleanComponent(challenge.started()))));
        lore.add(serializer.serialize(Component.text("Paused: ", gray).append(booleanComponent(challenge.paused()))));
        challenge.modifier(TimerModifier.class)
                .map(TimerModifier::playedTimeAsString)
                .ifPresent(time -> lore.add(serializer.serialize(Component.text("Time played: ", gray).append(Component.text(time, primary)))));

        lore.add("");
        lore.add(serializer.serialize(Component.text("Modifiers ", primary).append(Component.text("(%s)".formatted(modifierCount), gray)).append(Component.text(":", primary))));

        if (modifierCount == 0) {
            lore.add(LegacyComponentSerializer.legacySection().serialize(Component.text("No modifiers applied", NamedTextColor.RED, TextDecoration.ITALIC)));
        } else {
            modifiers.forEach(modifier -> lore.add(serializer.serialize(Component.text("- ", gray).append(Component.text(modifier.name(), primary)))));
        }

        item.setLore(lore);

        return new ChallengeGuiItem(challenge, item, event -> challenge.join(event.getPlayer()));
    }

    private TextComponent booleanComponent(boolean value) {
        String text = value ? "✔" : "✘";
        TextColor color = value ? NamedTextColor.GREEN : NamedTextColor.RED;
        return Component.text(text, color);
    }

    private static ItemStack skullItem() {
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        Objects.requireNonNull(item, "Item cannot be null");
        ItemMeta itemMeta = SkullUtils.applySkin(item.getItemMeta(), "f151cffdaf303673531a7651b36637cad912ba485643158e548d59b2ead5011");
        item.setItemMeta(itemMeta);
        return item;
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
