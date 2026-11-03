package me.noci.challenges.gui.modifier;

public class GuiModifierOverview {

   /* private static final int[] PAGE_SLOTS = InventoryPattern.box(2, 3);

    private final ModifierApplier modifierApplier;
    private final ImmutableList<Class<? extends ChallengeModifier>> ignoredModifiers;

    public GuiModifierOverview(ModifierApplier modifierApplier, ImmutableList<Class<? extends ChallengeModifier>> ignoredModifiers) {
        super(Option.Gui.MODIFIER_OVERVIEW_TITLE.get(), 36);
        this.modifierApplier = modifierApplier;
        this.ignoredModifiers = ignoredModifiers;
    }

    @Override
    public void init(Player player, InventoryContent content) {
        content.fill(InventoryConstants.GLAS_PANE);
        content.fillSlots(GuiItem.empty(), PAGE_SLOTS);

        content.setItem(Slot.getSlot(4, 1), InventoryConstants.backItem().asGuiItem(event -> modifierApplier.cancel()));
    }

    @Override
    public void update(Player player, InventoryContent inventoryContent) {

    }

    @Override
    public void initPage(Player player, InventoryPageContent content) {
        content.itemSlots(PAGE_SLOTS);
        content.previousPageButton(Slot.getSlot(3, 1), InventoryConstants.previousPageItem(), InventoryConstants.GLAS_PANE.getItemStack());
        content.nextPageButton(Slot.getSlot(3, 1), InventoryConstants.nextPageItem(), InventoryConstants.GLAS_PANE.getItemStack());

        GuiItem[] items = ModifierRegistry.modifiers()
                .filter(modifier -> !ignoredModifiers.contains(modifier.type()))
                .map(modifier -> {
                    ItemBuilder item = ModifierRegistry.displayItem(modifier.type());

                    return item.asGuiItem(event -> {
                        if (event.getClick() != ClickType.LEFT) return;
                        modifier.create(player, modifierApplier);
                    });
                })
                .toArray(GuiItem[]::new);

        content.pageContent(items);
    }

    @Override
    public void updatePageContent(Player player, InventoryPageContent inventoryPageContent) {

    }*/

}
