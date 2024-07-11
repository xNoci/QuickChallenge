package me.noci.challenges.gui;

import com.google.common.collect.Lists;
import me.noci.challenges.QuickChallenge;
import me.noci.challenges.challenge.modifiers.allitem.AllItem;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.allitem.CollectedItem;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.inventory.*;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.QuickItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class GuiAllItemOverview extends PagedQuickGUIProvider {

    private final AllItemModifier modifier;

    public GuiAllItemOverview(AllItemModifier modifier) {
        super(QuickChallenge.instance().config().get(
                Option.Gui.AllItems.TITLE,
                TagResolver.builder().resolvers(
                        Formatter.number("items_collected", modifier.collectedItems().size()),
                        Formatter.number("total_items", AllItem.values().length)
                ).build()
        ), InventoryConstants.FULL_SIZE);


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
        pageContent.setPreviousPageItem(Slot.getSlot(3, 1), InventoryConstants.previousPageItem(), InventoryConstants.GLAS_PANE.getItemStack());
        pageContent.setNextPageItem(Slot.getSlot(3, 9), InventoryConstants.nextPageItem(), InventoryConstants.GLAS_PANE.getItemStack());
    }

    private static GuiItem toGuiItem(CollectedItem collectedItem) {
        Config config = QuickChallenge.instance().config();
        AllItem allItem = collectedItem.item();
        var item = new QuickItemStack(allItem.material(), config.get(Option.Gui.AllItems.ITEM_NAME, Placeholder.unparsed("item_name", allItem.itemName())));
        item.addItemFlags();

        LocalDateTime timestamp = Instant.ofEpochMilli(collectedItem.timestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        Component collected_at = config.get(Option.Gui.AllItems.COLLECTED_AT, Formatter.date("collection_time", timestamp));

        Option<Component> option = collectedItem.skipped() ? Option.Gui.AllItems.SKIPPED_BY : Option.Gui.AllItems.COLLECTED_BY;
        Component collectedBy = config.get(option, Placeholder.unparsed("collector", collectedItem.collectedBy()));
        if (collectedItem.collectedAfterTicks() >= 0) {
            TagResolver timestampResolver = Placeholder.unparsed("time", collectedItem.collectedAfter());
            collectedBy = collectedBy.append(config.get(Option.Gui.AllItems.TIMESTAMP_SUFFIX, timestampResolver));
        }

        List<Component> lore = Lists.newArrayList();
        lore.add(Component.empty());
        lore.add(collected_at);
        lore.add(collectedBy);

        item.itemLore(lore);
        return item.asGuiItem();
    }

}
