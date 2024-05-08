package me.noci.challenges.challenge.modifiers.allitem;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import lombok.Getter;
import me.noci.challenges.RandomHolder;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.colors.ColorUtils;
import me.noci.challenges.colors.Colors;
import me.noci.challenges.serializer.TypeSerializer;
import me.noci.quickutilities.events.Events;
import me.noci.quickutilities.events.subscriber.SubscribedEvent;
import me.noci.quickutilities.utils.EnumUtils;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.Logger;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static me.noci.challenges.serializer.TypeSerializers.*;

public class AllItemModifier implements ChallengeModifier {

    public static final TypeSerializer<Optional<AllItemModifier>> SERIALIZER = TypeSerializer.dynamic(value -> BOOLEAN.byteSize(null) +
            COLLECTED_ITEM_LIST.byteSize(value.map(AllItemModifier::collectedItems).orElse(List.of())) +
            ALL_ITEM.byteSize(null) +
            BOOLEAN.byteSize(null), buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        List<CollectedItem> collectedItems = COLLECTED_ITEM_LIST.read(buffer);
        AllItem currentItem = ALL_ITEM.read(buffer);
        boolean allItemsCollected = BOOLEAN.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new AllItemModifier(currentItem, collectedItems, allItemsCollected));
    }, (buffer, value) -> {
        BOOLEAN.write(buffer, value.isPresent());
        COLLECTED_ITEM_LIST.write(buffer, value.map(AllItemModifier::collectedItems).orElse(List.of()));
        ALL_ITEM.write(buffer, value.map(AllItemModifier::currentItem).orElse(AllItem.ACACIA_BOAT));
        BOOLEAN.write(buffer, value.map(AllItemModifier::allItemsCollected).orElse(false));
    });

    private final BossBar spacerBar;
    private final BossBar bossBar;
    @Getter private final List<CollectedItem> collectedItems;
    @Getter @NotNull private AllItem currentItem;
    @Getter private boolean allItemsCollected;
    private SubscribedEvent<PlayerInventorySlotChangeEvent> slotChangeEvent;
    private SubscribedEvent<InventoryClickEvent> inventoryClickEvent;

    public AllItemModifier() {
        this(EnumUtils.random(AllItem.class), Lists.newArrayList(), false);
    }

    public AllItemModifier(@NotNull AllItem currentItem, List<CollectedItem> collectedItems, boolean allItemsCollected) {
        this.currentItem = currentItem;
        this.collectedItems = collectedItems;
        this.allItemsCollected = allItemsCollected;
        this.spacerBar = BossBar.bossBar(Component.empty(), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
        this.bossBar = BossBar.bossBar(itemDisplay(), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
        if (slotChangeEvent != null) {
            slotChangeEvent.unsubscribe();
        }

        slotChangeEvent = Events.subscribe(PlayerInventorySlotChangeEvent.class)
                .filter(event -> !challenge.paused())
                .filter(event -> currentItem.matches(event.getNewItemStack()))
                .handle(event -> tryPickupItem(challenge, event.getPlayer(), currentItem));

        if (inventoryClickEvent != null) {
            inventoryClickEvent.unsubscribe();
        }

        inventoryClickEvent = Events.subscribe(InventoryClickEvent.class)
                .strict(false)
                .filter(Predicate.not(InventoryInteractEvent::isCancelled))
                .filter(event -> event.getClickedInventory() != null)
                .filter(event -> Require.nonNull(event.getClickedInventory()).getType() != InventoryType.CREATIVE)
                .filter(event -> event.getCurrentItem() != null)
                .filter(event -> switch (event.getClick()) {
                    case LEFT, RIGHT -> true;
                    default -> false;
                })
                .filter(event -> event.getWhoClicked() instanceof Player)
                .filter(event -> !challenge.paused())
                .filter(event -> currentItem.matches(Require.nonNull(event.getCurrentItem())))
                .handle(event -> tryPickupItem(challenge, (Player) event.getWhoClicked(), currentItem));

    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
        removeBossBarViewers(player -> true);

        if (slotChangeEvent != null) {
            slotChangeEvent.unsubscribe();
            slotChangeEvent = null;
        }

        if (inventoryClickEvent != null) {
            inventoryClickEvent.unsubscribe();
            inventoryClickEvent = null;
        }
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {
        addBossBarViewers(players);
        removeBossBarViewers(Predicate.not(players::contains));
    }

    @Override
    public String name() {
        return "All Items";
    }

    private void tryPickupItem(Challenge challenge, Player collector, AllItem item) {
        if (allItemsCollected) return;
        if (item != currentItem) return;
        collectedItems.add(CollectedItem.now(item));
        notifyItemsCollected(challenge, collector, item, false);
        nextItem(challenge);
    }

    public void skip(Challenge challenge, Player player) {
        if (allItemsCollected) return;
        collectedItems.add(CollectedItem.now(currentItem));
        notifyItemsCollected(challenge, player, currentItem, true);
        nextItem(challenge);
    }

    private void nextItem(Challenge challenge) {
        List<AllItem> remainingItems = remainingItems();
        if (remainingItems.isEmpty()) {
            allItemsCollected = true;
        } else {
            currentItem = remainingItems.get(RandomHolder.random().nextInt(remainingItems.size()));
            broadcastNextItem(challenge, currentItem);
            checkAllPlayerInventories(challenge);
        }

        bossBar.name(itemDisplay());
    }

    private List<AllItem> remainingItems() {
        EnumSet<AllItem> alreadyCollected = Sets.newEnumSet(collectedItems.stream().map(CollectedItem::item).toList(), AllItem.class);

        return EnumUtils.asStream(AllItem.class)
                .filter(Predicate.not(alreadyCollected::contains))
                .toList();
    }

    private Component itemDisplay() {
        Component display = Component.empty();

        if (allItemsCollected) {
            display = display.append(Component.text("Alle Items eingesammelt!", NamedTextColor.WHITE, TextDecoration.BOLD, TextDecoration.ITALIC));
        } else {
            display = display.append(currentItem.icon())
                    .append(Component.space())
                    .append(Component.text(currentItem.itemName(), NamedTextColor.WHITE, TextDecoration.BOLD, TextDecoration.ITALIC));
        }

        return display.append(Component.space()).append(Component.text("(%s/%s)".formatted(collectedItems.size(), AllItem.values().length)));
    }

    private void checkAllPlayerInventories(Challenge challenge) {
        for (Player player : challenge.players()) {
            var inventory = player.getInventory();
            List<ItemStack> items = Lists.newArrayList(inventory.getContents());
            items.addAll(Arrays.asList(inventory.getArmorContents()));

            for (ItemStack itemStack : items) {
                if (currentItem.matches(itemStack)) {
                    tryPickupItem(challenge, player, currentItem);
                    return;
                }
            }
        }
    }

    private static void notifyItemsCollected(Challenge challenge, Player collector, AllItem item, boolean skipped) {
        challenge.players().forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1, 1));

        Component messageItemCollected;
        if (!skipped) {
            messageItemCollected = Component
                    .text("Der Spieler ", Colors.GRAY)
                    .append(collector.name().color(NamedTextColor.AQUA))
                    .append(Component.text(" hat das Item ", Colors.GRAY))
                    .append(ColorUtils.gradientText(item.itemName(), Colors.TIMER_PRIMARY_COLOR, Colors.TIMER_ACCENT_COLOR))
                    .append(Component.text(" aufgesammelt.", Colors.GRAY))
                    .asComponent();
        } else {
            messageItemCollected = Component
                    .text("Das Item ", Colors.GRAY)
                    .append(ColorUtils.gradientText(item.itemName(), Colors.TIMER_PRIMARY_COLOR, Colors.TIMER_ACCENT_COLOR))
                    .append(Component.text(" wurde von ", Colors.GRAY))
                    .append(collector.name().color(NamedTextColor.AQUA))
                    .append(Component.text(" übersprungen.", Colors.GRAY));
        }

        challenge.broadcast(messageItemCollected);
    }

    private static void broadcastNextItem(Challenge challenge, AllItem item) {
        Component messageNewItem = Component
                .text("Das nächste Item ist: ", Colors.GRAY)
                .append(ColorUtils.gradientText(item.itemName(), Colors.TIMER_PRIMARY_COLOR, Colors.TIMER_ACCENT_COLOR))
                .asComponent();

        challenge.broadcast(messageNewItem);
    }

    public void reset() {
        this.collectedItems.clear();
        this.allItemsCollected = false;
        currentItem = EnumUtils.random(AllItem.class);
        bossBar.name(itemDisplay());
    }

    private void addBossBarViewers(List<Player> viewers) {
        viewers.forEach(viewer -> {
            spacerBar.addViewer(viewer);
            bossBar.addViewer(viewer);
        });
    }

    private void removeBossBarViewers(Predicate<Player> filter) {
        Lists.newArrayList(spacerBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .filter(filter)
                .forEach(spacerBar::removeViewer);

        Lists.newArrayList(bossBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .filter(filter)
                .forEach(bossBar::removeViewer);
    }

}
