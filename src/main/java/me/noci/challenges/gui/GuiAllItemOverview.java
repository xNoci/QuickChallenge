package me.noci.challenges.gui;

import com.google.common.collect.Lists;
import me.noci.challenges.challenge.modifiers.allitem.AllItem;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.allitem.CollectedItem;
import me.noci.challenges.colors.ColorUtils;
import me.noci.challenges.colors.Colors;
import me.noci.quickutilities.inventory.*;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.QuickItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GuiAllItemOverview extends PagedQuickGUIProvider {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private final AllItemModifier modifier;

    public GuiAllItemOverview(AllItemModifier modifier) {
        super(Component.text("Item übersicht (%s von %s)".formatted(modifier.collectedItems().size(), AllItem.values().length), Colors.GUI_TITLE), InventoryConstants.FULL_SIZE);
        this.modifier = modifier;
    }

    @Override
    public void init(Player player, InventoryContent inventoryContent) {
        inventoryContent.fillBorders(InventoryConstants.GLAS_PANE);
    }

    @Override
    public void initPage(Player player, PageContent pageContent) {
        var items = modifier.collectedItems().stream().map(GuiAllItemOverview::toGuiItem).toArray(GuiItem[]::new);
        pageContent.setPageContent(items);

        pageContent.setItemSlots(InventoryPattern.box(2, 5));
        pageContent.setPreviousPageItem(Slot.getSlot(3, 1), InventoryConstants.PREVIOUS_PAGE, InventoryConstants.GLAS_PANE.getItemStack());
        pageContent.setNextPageItem(Slot.getSlot(3, 9), InventoryConstants.NEXT_PAGE, InventoryConstants.GLAS_PANE.getItemStack());
    }

    private static GuiItem toGuiItem(CollectedItem collectedItem) {
        AllItem allItem = collectedItem.item();
        var item = new QuickItemStack(allItem.material(), ColorUtils.gradientText(allItem.itemName(), Colors.TIMER_PRIMARY_COLOR, Colors.TIMER_ACCENT_COLOR).decorate(TextDecoration.BOLD)).addItemFlags();

        Date collectionDate = new Date(collectedItem.timestamp());


        List<Component> lore = Lists.newArrayList();
        lore.add(Component.empty());
        lore.add(Component.text("Eingesammelt am ", Colors.GRAY)
                .append(Component.text(DATE_FORMAT.format(collectionDate), Colors.PRIMARY))
                .append(Component.text(" um ", Colors.GRAY))
                .append(Component.text(TIME_FORMAT.format(collectionDate), Colors.PRIMARY))
                .append(Component.text(" Uhr", Colors.GRAY)));

        Component collectedBy = getCollectedBy(collectedItem);
        lore.add(collectedBy);

        item.itemLore(lore);
        return item.asGuiItem();
    }

    private static @NotNull Component getCollectedBy(CollectedItem collectedItem) {
        Component collectedBy;

        if (!collectedItem.skipped()) {
            collectedBy = Component.text("Aufgesammelt von ", Colors.GRAY);
        } else {
            collectedBy = Component.text("Übersprungen von ", Colors.GRAY, TextDecoration.ITALIC);
        }

        collectedBy = collectedBy.append(Component.text(collectedItem.collectedBy(), Colors.PRIMARY));

        if (collectedItem.collectedAfterTicks() >= 0) {
            collectedBy = collectedBy.append(Component.text(" nach ", Colors.GRAY))
                    .append(Component.text(collectedItem.collectedAfter(), Colors.PRIMARY));
        }

        return collectedBy;
    }
}
