package me.noci.challenges.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.noci.challenges.challenge.modifiers.allitem.AllItem;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.allitem.CollectedItem;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.gui.Gui;
import me.noci.quickutilities.gui.GuiItem;
import me.noci.quickutilities.gui.PagedGui;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.ItemBuilder;
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

public class GuiAllItemOverview {

    public static void openGui(Player player, AllItemModifier modifier) {
        Component title = Option.Gui.AllItems.TITLE.resolve(
                Formatter.number("items_collected", modifier.collectedItems().size()),
                Formatter.number("total_items", AllItem.values().length)
        );

        PagedGui gui = Gui.paged()
                .title(title)
                .rows(6)
                .create();

        gui.fillPattern(InventoryConstants.GLAS_PANE, InventoryPattern.BORDER);
        gui.setItem(4, statsDisplay(modifier));

        gui.setItem(1, 3, InventoryConstants.previousPageItem().asGuiItem(_ -> gui.previous()));
        gui.setItem(9, 3, InventoryConstants.nextPageItem().asGuiItem(_ -> gui.next()));

        GuiItem[] collectedItems = modifier.collectedItems().stream().map(GuiAllItemOverview::toGuiItem)
                .toArray(GuiItem[]::new);
        gui.addItem(collectedItems);

        gui.open(player);
    }

    private static GuiItem statsDisplay(AllItemModifier modifier) {
        ItemBuilder itemBuilder = ItemBuilder.of(Material.CHERRY_SIGN, Option.Gui.AllItems.STATS_DISPLAYNAME.get());

        HashMap<String, CollectionData> playerStats = Maps.newHashMap();
        modifier.collectedItems().forEach(collectedItem -> {
            int addToCollected = !collectedItem.skipped() ? 1 : 0;
            int addToSkip = collectedItem.skipped() ? 1 : 0;
            BiFunction<String, CollectionData, CollectionData> merge = (_, data) -> data == null ?
                    new CollectionData(addToCollected, addToSkip) : data.add(addToCollected, addToSkip);
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

                    Option<Component> line = data.skipped > 0 ? Option.Gui.AllItems.STATS_ENTRY_SKIPPED :
                            Option.Gui.AllItems.STATS_ENTRY;
                    return line.resolve(resolver);
                }).collect(Collectors.toCollection(ArrayList::new));
        lore.addFirst(Component.empty());
        return itemBuilder.lore(lore).asGuiItem();
    }

    private static GuiItem toGuiItem(CollectedItem collectedItem) {
        AllItem allItem = collectedItem.item();
        ItemBuilder item = ItemBuilder.of(allItem.material())
                .displayName(
                        Option.Gui.AllItems.ITEM_NAME.resolve(Placeholder.component("item_name", allItem.itemName())))
                .itemFlags();

        LocalDateTime timestamp =
                Instant.ofEpochMilli(collectedItem.timestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        Component collected_at = Option.Gui.AllItems.COLLECTED_AT.resolve(Formatter.date("collection_time", timestamp));

        Option<Component> component =
                collectedItem.skipped() ? Option.Gui.AllItems.SKIPPED_BY : Option.Gui.AllItems.COLLECTED_BY;
        Component collectedBy = component.resolve(Placeholder.unparsed("collector", collectedItem.collectedBy()));
        if (collectedItem.collectedAfterTicks() >= 0) {
            TagResolver timestampResolver = Placeholder.unparsed("time", collectedItem.collectedAfter());
            collectedBy = collectedBy.append(Option.Gui.AllItems.TIMESTAMP_SUFFIX.resolve(timestampResolver));
        }

        List<Component> lore = Lists.newArrayList();
        lore.add(Component.empty());
        lore.add(collected_at);
        lore.add(collectedBy);

        return item.lore(lore).asGuiItem();
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
