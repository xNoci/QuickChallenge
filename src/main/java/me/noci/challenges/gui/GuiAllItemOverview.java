package me.noci.challenges.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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

        Config config = QuickChallenge.instance().config();
        QuickItemStack item = new QuickItemStack(Material.CHERRY_SIGN, config.get(Option.Gui.AllItems.STATS_DISPLAYNAME));


        HashMap<String, CollectionData> playerStats = Maps.newHashMap();
        modifier.collectedItems().forEach(collectedItem -> {
            int addToCollected = !collectedItem.skipped() ? 1 : 0;
            int addToSkip = collectedItem.skipped() ? 1 : 0;
            BiFunction<String, CollectionData, CollectionData> merge = (key, data) -> data == null ? new CollectionData(addToCollected, addToSkip) : data.add(addToCollected, addToSkip);
            playerStats.compute(collectedItem.collectedBy(), merge);
        });


        List<Component> lore = playerStats.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> {
                    CollectionData data = entry.getValue();

                    TagResolver resolver = TagResolver.builder()
                            .resolvers(
                                    Placeholder.unparsed("collector", entry.getKey()),
                                    Formatter.number("total", data.totalAmount()),
                                    Formatter.number("skipped", data.skipped())
                            )
                            .build();

                    Option<Component> line = data.skipped > 0 ? Option.Gui.AllItems.STATS_ENTRY_SKIPPED : Option.Gui.AllItems.STATS_ENTRY;
                    return config.get(line, resolver);
                }).collect(Collectors.toCollection(ArrayList::new));

        lore.addFirst(Component.empty());
        item.lore(lore);

        inventoryContent.setItem(4, item.asGuiItem());

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

    private record CollectionData(int collected, int skipped) implements Comparable<CollectionData> {
        private int totalAmount() {
            return collected + skipped;
        }

        private CollectionData add(int collected, int skipped) {
            return new CollectionData(this.collected + collected, this.skipped + skipped);
        }

        @Override
        public int compareTo(CollectionData o) {
            return Integer.compare(o.totalAmount(), this.totalAmount());
        }
    }

}
