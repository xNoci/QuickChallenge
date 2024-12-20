package me.noci.challenges.challenge.modifiers.allitem;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import lombok.Getter;
import me.noci.challenges.RandomHolder;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.headcomponent.HeadComponent;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.events.Events;
import me.noci.quickutilities.events.subscriber.SubscribedEvent;
import me.noci.quickutilities.utils.EnumUtils;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.logging.log4j.Logger;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AllItemModifier implements ChallengeModifier {

    private final BossBar bossBar;
    @Getter private final List<CollectedItem> collectedItems;
    @Getter @NotNull private AllItem currentItem;
    @Getter private boolean allItemsCollected;
    private SubscribedEvent<PlayerInventorySlotChangeEvent> slotChangeEvent;
    private SubscribedEvent<InventoryClickEvent> inventoryClickEvent;

    private Component currentDisplay;

    public AllItemModifier() {
        this(EnumUtils.random(AllItem.class), Lists.newArrayList(), false);
    }

    public AllItemModifier(@NotNull AllItem currentItem, List<CollectedItem> collectedItems, boolean allItemsCollected) {
        this.currentItem = currentItem;
        this.collectedItems = collectedItems;
        this.allItemsCollected = allItemsCollected;
        this.bossBar = BossBar.bossBar(itemDisplay(), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    private static void notifyItemsCollected(Challenge challenge, CommandSender collector, AllItem item, boolean skipped) {
        challenge.players().forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1, 1));

        boolean isPlayer = false;
        Component headComponent = Component.empty();
        if (collector instanceof Player player) {
            headComponent = HeadComponent.create(player.getUniqueId()).build();
            isPlayer = true;
        }

        Option<Component> component = skipped ? Option.AllItems.Chat.ITEM_SKIPPED : Option.AllItems.Chat.ITEM_COLLECTED;
        if (skipped && !isPlayer) {
            component = Option.AllItems.Chat.ITEM_SKIPPED_CONSOLE;
        }

        challenge.broadcast(component.resolve(
                Placeholder.component("player_head", headComponent),
                Placeholder.component("player", collector.name()),
                Placeholder.unparsed("item_name", item.itemName())
        ));
    }

    private static void broadcastNextItem(Challenge challenge, AllItem item) {
        var component = Option.AllItems.Chat.NEXT_ITEM.resolve(Placeholder.unparsed("item_name", item.itemName()));
        challenge.broadcast(component);
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
                .filter(event -> event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)
                .filter(event -> event.getWhoClicked() instanceof Player)
                .filter(event -> !challenge.paused())
                .filter(event -> currentItem.matches(Require.nonNull(event.getCurrentItem())))
                .handle(event -> tryPickupItem(challenge, (Player) event.getWhoClicked(), currentItem));
    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
        Lists.newArrayList(bossBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .forEach(bossBar::removeViewer);

        if (slotChangeEvent != null) {
            slotChangeEvent.unsubscribe();
            slotChangeEvent = null;
        }

        if (inventoryClickEvent != null) {
            inventoryClickEvent.unsubscribe();
            inventoryClickEvent = null;
        }

        currentDisplay = null;
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {
        players.forEach(bossBar::addViewer);
        Lists.newArrayList(bossBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .filter(Predicate.not(players::contains))
                .forEach(bossBar::removeViewer);

        bossBar.name(itemDisplay());
    }

    @Override
    public void onConfigReload(Logger logger, Challenge challenge, Config config) {
        currentDisplay = null;
    }

    @Override
    public String name() {
        return "All Items";
    }

    private void tryPickupItem(Challenge challenge, Player player, AllItem item) {
        if (allItemsCollected) return;
        if (item != currentItem) return;
        long ticksPlayed = challenge.modifier(TimerModifier.class, TimerModifier::ticksPlayed, -1L);
        collectedItems.add(CollectedItem.now(item, player.getName(), ticksPlayed, false));
        notifyItemsCollected(challenge, player, item, false);
        nextItem(challenge);
    }

    public void skip(Challenge challenge, CommandSender collector) {
        if (allItemsCollected) return;
        long ticksPlayed = challenge.modifier(TimerModifier.class, TimerModifier::ticksPlayed, -1L);
        collectedItems.add(CollectedItem.now(currentItem, collector.getName(), ticksPlayed, true));
        notifyItemsCollected(challenge, collector, currentItem, true);
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
        currentDisplay = null;
    }

    private List<AllItem> remainingItems() {
        EnumSet<AllItem> alreadyCollected = Sets.newEnumSet(collectedItems.stream().map(CollectedItem::item).toList(), AllItem.class);

        return EnumUtils.asStream(AllItem.class)
                .filter(Predicate.not(alreadyCollected::contains))
                .toList();
    }

    private Component itemDisplay() {
        if (currentDisplay != null) return currentDisplay;

        int collectedItemCount = collectedItems.size();
        int itemsToCollectCount = AllItem.values().length;

        Option<Component> component = allItemsCollected ? Option.AllItems.BossBar.COMPLETE : Option.AllItems.BossBar.NEXT_ITEM;
        
        currentDisplay = component.resolve(
                Placeholder.component("item_icon", currentItem.icon()),
                Placeholder.unparsed("item_name", currentItem.itemName()),
                Formatter.number("items_collected", collectedItemCount),
                Formatter.number("total_items", itemsToCollectCount),
                Formatter.number("progress", (float) collectedItemCount / itemsToCollectCount * 100)
        );
        return currentDisplay;
    }

    private void checkAllPlayerInventories(Challenge challenge) {
        for (Player player : challenge.players()) {
            var inventory = player.getInventory();

            Stream.of(inventory.getContents(), inventory.getArmorContents())
                    .flatMap(Stream::of)
                    .filter(itemStack -> currentItem.matches(itemStack))
                    .findAny()
                    .ifPresent(itemStack -> tryPickupItem(challenge, player, currentItem));
        }
    }

    public void reset() {
        this.collectedItems.clear();
        this.allItemsCollected = false;
        currentItem = EnumUtils.random(AllItem.class);
        currentDisplay = null;
    }

}
