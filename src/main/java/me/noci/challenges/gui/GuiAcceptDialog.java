package me.noci.challenges.gui;

import lombok.Getter;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.inventory.*;
import me.noci.quickutilities.utils.QuickItemStack;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiAcceptDialog extends QuickGUIProvider {

    private static final Consumer<SlotClickEvent> EMPTY_CLICK = event -> {
    };

    private final GuiItem acceptItem;
    private final GuiItem declineItem;
    private final GuiItem descriptionItem;

    private GuiAcceptDialog(Component title, GuiItem acceptItem, GuiItem declineItem, GuiItem descriptionItem) {
        super(title, 9 * 3);
        this.acceptItem = acceptItem;
        this.declineItem = declineItem;
        this.descriptionItem = descriptionItem;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void init(Player player, InventoryContent inventoryContent) {
        inventoryContent.fill(InventoryConstants.GLAS_PANE);

        inventoryContent.setItem(Slot.getSlot(2, 3), acceptItem);
        inventoryContent.setItem(Slot.getSlot(2, 5), descriptionItem);
        inventoryContent.setItem(Slot.getSlot(2, 7), declineItem);
    }

    @Getter
    public enum DialogType {
        YES_NO(() -> Option.Gui.Dialog.YES_ITEM, () -> Option.Gui.Dialog.NO_ITEM),
        ACCEPT_DECLINE(() -> Option.Gui.Dialog.ACCEPT_ITEM, () -> Option.Gui.Dialog.DECLINE_ITEM);

        private final Supplier<Option<Component>> acceptSupplier;
        private final Supplier<Option<Component>> declineSupplier;

        DialogType(Supplier<Option<Component>> acceptSupplier, Supplier<Option<Component>> declineSupplier) {
            this.acceptSupplier = acceptSupplier;
            this.declineSupplier = declineSupplier;
        }

        public Component acceptName() {
            return acceptSupplier.get().get();
        }

        public Component declineName() {
            return declineSupplier.get().get();
        }

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
            var acceptItem = new QuickItemStack(Material.GREEN_WOOL, this.dialogType.acceptName());
            var declineItem = new QuickItemStack(Material.RED_WOOL, this.dialogType.declineName());
            var descriptionItem = new QuickItemStack(Material.OAK_SIGN, Option.Gui.Dialog.DESCRIPTION_ITEM.get());

            List<Component> lore = this.lore != null ? this.lore : List.of();
            descriptionItem.lore(lore);

            Consumer<SlotClickEvent> onAccept = this.onAccept != null ? this.onAccept : EMPTY_CLICK;
            Consumer<SlotClickEvent> onDecline = this.onDecline != null ? this.onDecline : EMPTY_CLICK;

            var gui = new GuiAcceptDialog(title, acceptItem.asGuiItem(onlyLeftClick(onAccept)), declineItem.asGuiItem(onlyLeftClick(onDecline)), descriptionItem.asGuiItem());
            gui.provide(player);
        }

        private ClickHandler onlyLeftClick(Consumer<SlotClickEvent> clickEvent) {
            return event -> {
                if (event.getClick() != ClickType.LEFT) return;
                clickEvent.accept(event);
            };
        }

    }


}
