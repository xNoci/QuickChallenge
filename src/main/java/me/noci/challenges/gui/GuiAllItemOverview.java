package me.noci.challenges.gui;

public class GuiAllItemOverview {

  /*  private final AllItemModifier modifier;

    public GuiAllItemOverview(AllItemModifier modifier) {
        super(Option.Gui.AllItems.TITLE.resolve(Formatter.number("items_collected", modifier.collectedItems().size()),
                Formatter.number("total_items", AllItem.values().length)), InventoryConstants.FULL_SIZE);

        this.modifier = modifier;
    }

    private static GuiItem toGuiItem(CollectedItem collectedItem) {
        AllItem allItem = collectedItem.item();
        ItemBuilder item = ItemBuilder.of(allItem.material(),
                Option.Gui.AllItems.ITEM_NAME.resolve(Placeholder.component("item_name", allItem.itemName())));
        item.withItemFlags();

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

        item.withLore(lore);
        return item.asGuiItem();
    }

    @Override
    public void init(Player player, InventoryContent inventoryContent) {
        inventoryContent.fillBorders(InventoryConstants.GLAS_PANE);

        ItemBuilder item = ItemBuilder.of(Material.CHERRY_SIGN, Option.Gui.AllItems.STATS_DISPLAYNAME.get());

        HashMap<String, CollectionData> playerStats = Maps.newHashMap();
        modifier.collectedItems().forEach(collectedItem -> {
            int addToCollected = !collectedItem.skipped() ? 1 : 0;
            int addToSkip = collectedItem.skipped() ? 1 : 0;
            BiFunction<String, CollectionData, CollectionData> merge =
                    (key, data) -> data == null ? new CollectionData(addToCollected, addToSkip)
                            : data.add(addToCollected, addToSkip);
            playerStats.compute(collectedItem.collectedBy(), merge);
        });

        List<Component> lore = playerStats.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(entry -> {
            CollectionData data = entry.getValue();

            TagResolver resolver = TagResolver.builder().resolvers(Placeholder.unparsed("collector", entry.getKey()),
                    Formatter.number("total", data.totalAmount()), Formatter.number("skipped", data.skipped())).build();

            Option<Component> line =
                    data.skipped > 0 ? Option.Gui.AllItems.STATS_ENTRY_SKIPPED : Option.Gui.AllItems.STATS_ENTRY;
            return line.resolve(resolver);
        }).collect(Collectors.toCollection(ArrayList::new));

        lore.addFirst(Component.empty());
        item.withLore(lore);

        inventoryContent.setItem(4, item.asGuiItem());

    }

    @Override
    public void update(Player player, InventoryContent inventoryContent) {

    }

    @Override
    public void initPage(Player player, InventoryPageContent pageContent) {
        var items = modifier.collectedItems().stream().map(GuiAllItemOverview::toGuiItem).toArray(GuiItem[]::new);
        pageContent.addContent(items);

        pageContent.itemSlots(InventoryPattern.box(2, 5));
        pageContent.previousPageButton(Slot.getSlot(3, 1), InventoryConstants.previousPageItem(),
                InventoryConstants.GLAS_PANE.getItemStack());
        pageContent.nextPageButton(Slot.getSlot(3, 9), InventoryConstants.nextPageItem(),
                InventoryConstants.GLAS_PANE.getItemStack());
    }

    @Override
    public void updatePageContent(Player player, InventoryPageContent inventoryPageContent) {

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
    }*/

}
