package me.noci.challenges.gui;

import me.noci.challenges.settings.Option;
import me.noci.quickutilities.gui.Gui;
import me.noci.quickutilities.gui.GuiAction;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.ItemBuilder;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class GuiAcceptDialog {

    public static GuiAcceptDialog builder() {
        return new GuiAcceptDialog();
    }

    private GuiAcceptDialog() {

    }

    @NotNull
    private DialogType dialogType = DialogType.YES_NO;
    @Nullable
    private Component title = null;
    @Nullable
    private List<Component> lore = null;
    @Nullable
    private GuiAction<InventoryClickEvent> onAccept = null;
    @Nullable
    private GuiAction<InventoryClickEvent> onDecline = null;

    public GuiAcceptDialog dialogType(DialogType dialogType) {
        Require.nonNull(dialogType, "dialogType cannot be null");
        this.dialogType = dialogType;
        return this;
    }

    public GuiAcceptDialog title(Component title) {
        Require.nonNull(title, "title cannot be null");
        Require.checkState(this.title == null, "A title is already set.");
        this.title = title;
        return this;
    }

    public GuiAcceptDialog description(List<Component> lore) {
        Require.nonNull(lore, "lore cannot be null");
        Require.checkState(this.lore == null, "A lore is already set.");
        this.lore = lore;
        return this;
    }

    public GuiAcceptDialog description(Component... lore) {
        Require.nonNull(lore, "lore cannot be null");
        Require.checkState(this.lore == null, "A lore is already set.");
        this.lore = List.of(lore);
        return this;
    }

    public GuiAcceptDialog acceptAction(GuiAction<InventoryClickEvent> onAccept) {
        Require.nonNull(onAccept, "onAccept cannot be null");
        Require.checkState(this.onAccept == null, "A decline action is already set.");
        this.onAccept = onAccept;
        return this;
    }

    public GuiAcceptDialog declineAction(GuiAction<InventoryClickEvent> onDecline) {
        Require.nonNull(onDecline, "onDecline cannot be null");
        Require.checkState(this.onDecline == null, "A decline action is already set.");
        this.onDecline = onDecline;
        return this;
    }

    public GuiAcceptDialog closeOnDecline() {
        Require.checkState(this.onDecline == null, "Cannot set close on decline, a decline action is already set.");
        onDecline = event -> event.getWhoClicked().closeInventory();
        return this;
    }

    public void open(HumanEntity entity) {
        Component title = this.title != null ? this.title : Component.text("");
        ItemBuilder acceptItem = ItemBuilder.of(Material.GREEN_WOOL, this.dialogType.acceptName());
        ItemBuilder declineItem = ItemBuilder.of(Material.RED_WOOL, this.dialogType.declineName());
        ItemBuilder descriptionItem = ItemBuilder.of(Material.OAK_SIGN, Option.Gui.Dialog.DESCRIPTION_ITEM.get());

        List<Component> lore = this.lore != null ? this.lore : List.of();
        descriptionItem.lore(lore);

        Gui gui = Gui.gui()
                .rows(3)
                .title(title)
                .create();

        gui.fillPattern(InventoryConstants.GLAS_PANE, InventoryPattern.FULL);
        gui.setItem(2, 2, acceptItem.asGuiItem(onlyLeftClick(onAccept)));
        gui.setItem(5, 2, descriptionItem.asGuiItem());
        gui.setItem(7, 2, declineItem.asGuiItem(onlyLeftClick(onDecline)));

        gui.open(entity);
    }

    private GuiAction<InventoryClickEvent> onlyLeftClick(GuiAction<InventoryClickEvent> clickEvent) {
        return event -> {
            if (clickEvent == null) return;
            if (event.getClick() != ClickType.LEFT) return;
            clickEvent.execute(event);
        };
    }

    public enum DialogType {
        YES_NO(Option.Gui.Dialog.YES_ITEM::get, Option.Gui.Dialog.NO_ITEM::get),
        ACCEPT_DECLINE(Option.Gui.Dialog.ACCEPT_ITEM::get, Option.Gui.Dialog.DECLINE_ITEM::get);

        private final Supplier<Component> acceptSupplier;
        private final Supplier<Component> declineSupplier;

        DialogType(Supplier<Component> acceptSupplier, Supplier<Component> declineSupplier) {
            this.acceptSupplier = acceptSupplier;
            this.declineSupplier = declineSupplier;
        }

        public Component acceptName() {
            return acceptSupplier.get();
        }

        public Component declineName() {
            return declineSupplier.get();
        }

    }

}
