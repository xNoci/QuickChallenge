package me.noci.challenges.gui;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.noci.quickutilities.inventory.*;
import me.noci.quickutilities.utils.QuickItemStack;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class GuiAcceptDialog extends QuickGUIProvider {

    private static final Consumer<SlotClickEvent> EMPTY_CLICK = event -> {
    };

    private final GuiItem acceptItem;
    private final GuiItem declineItem;
    private final GuiItem descriptionItem;

    public static Builder builder() {
        return new Builder();
    }

    private GuiAcceptDialog(Component title, GuiItem acceptItem, GuiItem declineItem, GuiItem descriptionItem) {
        super(title, 9 * 3);
        this.acceptItem = acceptItem;
        this.declineItem = declineItem;
        this.descriptionItem = descriptionItem;
    }

    @Override
    public void init(Player player, InventoryContent inventoryContent) {
        inventoryContent.fill(InventoryConstants.GLAS_PANE);

        inventoryContent.setItem(Slot.getSlot(2, 3), acceptItem);
        inventoryContent.setItem(Slot.getSlot(2, 5), descriptionItem);
        inventoryContent.setItem(Slot.getSlot(2, 7), declineItem);
    }

    public static class Builder {

        @NotNull private DialogType dialogType = DialogType.YES_NO;
        @Nullable private Component title = null;
        @Nullable private List<Component> lore = null;
        @Nullable private Consumer<SlotClickEvent> onAccept = null;
        @Nullable private Consumer<SlotClickEvent> onDecline = null;

        public Builder dialogType(DialogType dialogType) {
            Require.nonNull(dialogType, "dialogType cannot be null");
            this.dialogType = dialogType;
            return this;
        }

        public Builder title(Component title) {
            Require.nonNull(title, "title cannot be null");
            Require.checkState(this.title == null, "A title is already set.");
            this.title = title;
            return this;
        }

        public Builder description(List<Component> lore) {
            Require.nonNull(lore, "lore cannot be null");
            Require.checkState(this.lore == null, "A lore is already set.");
            this.lore = lore;
            return this;
        }

        public Builder description(Component... lore) {
            Require.nonNull(lore, "lore cannot be null");
            Require.checkState(this.lore == null, "A lore is already set.");
            this.lore = List.of(lore);
            return this;
        }

        public Builder acceptAction(Consumer<SlotClickEvent> onAccept) {
            Require.nonNull(onAccept, "onAccept cannot be null");
            Require.checkState(this.onAccept == null, "A decline action is already set.");
            this.onAccept = onAccept;
            return this;
        }

        public Builder declineAction(Consumer<SlotClickEvent> onDecline) {
            Require.nonNull(onDecline, "onDecline cannot be null");
            Require.checkState(this.onDecline == null, "A decline action is already set.");
            this.onDecline = onDecline;
            return this;
        }

        public Builder closeOnDecline() {
            Require.checkState(this.onDecline == null, "Cannot set close on decline, a decline action is already set.");
            onDecline = event -> event.getPlayer().closeInventory();
            return this;
        }

        public void provide(Player player) {
            var title = this.title != null ? this.title : Component.text("");
            var acceptItem = new QuickItemStack(XMaterial.GREEN_WOOL.parseMaterial(), this.dialogType.acceptTitle());
            var declineItem = new QuickItemStack(XMaterial.RED_WOOL.parseMaterial(), this.dialogType.declineTitle());
            var descriptionItem = new QuickItemStack(XMaterial.OAK_SIGN.parseMaterial(), Component.text("Beschreibung:", TextColor.color(99, 128, 101)));

            List<Component> lore = this.lore != null ? this.lore : List.of();
            descriptionItem.lore(lore);

            Consumer<SlotClickEvent> onAccept = this.onAccept != null ? this.onAccept : EMPTY_CLICK;
            Consumer<SlotClickEvent> onDecline = this.onDecline != null ? this.onDecline : EMPTY_CLICK;

            var gui = new GuiAcceptDialog(title, acceptItem.asGuiItem(onAccept::accept), declineItem.asGuiItem(onDecline::accept), descriptionItem.asGuiItem());
            gui.provide(player);
        }

    }

    @Getter
    public enum DialogType {
        YES_NO(Component.text("Ja", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text("Nein", NamedTextColor.RED, TextDecoration.BOLD)),
        ACCEPT_DECLINE(Component.text("Annehmen", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text("Ablehnen", NamedTextColor.RED, TextDecoration.BOLD));

        private final Component acceptTitle;
        private final Component declineTitle;

        DialogType(Component acceptTitle, Component declineTitle) {
            this.acceptTitle = acceptTitle;
            this.declineTitle = declineTitle;
        }
    }


}
