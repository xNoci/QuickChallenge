package me.noci.challenges.challenge.modifiers.allitem;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import me.noci.challenges.RandomHolder;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.utils.EnumUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AllItemModifier extends ChallengeModifier {


    private final BossBar bossBar;
    @Getter
    private final List<CollectedItem> collectedItems;
    @Getter
    @NotNull
    private AllItem currentItem;
    @Getter
    private boolean allItemsCollected;

    private Component currentDisplay;

    public AllItemModifier() {
        this(Lists.newArrayList(), EnumUtils.random(AllItem.class), false);
    }

    public AllItemModifier(List<CollectedItem> items, @NotNull AllItem current, boolean allCollected) {
        super("All Items");
        this.collectedItems = Lists.newArrayList(items);
        this.currentItem = current;
        this.allItemsCollected = allCollected;
        this.bossBar = BossBar.bossBar(itemDisplay(), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    @Override
    public void onInitialise() {

    }

    @Override
    public void onStop() {
        Lists.newArrayList(bossBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .forEach(bossBar::removeViewer);


        currentDisplay = null;
    }

    @Override
    public void onTick(Collection<? extends Player> players) {
        players.forEach(bossBar::addViewer);
        Lists.newArrayList(bossBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .filter(Predicate.not(players::contains))
                .forEach(bossBar::removeViewer);

        bossBar.name(itemDisplay());
    }

    @Override
    public void onConfigReload() {
        currentDisplay = null;
    }

    public void collectCurrentItem(CommandSender collector) {
        if (allItemsCollected) return;
        long ticksPlayed = getChallenge().modifier(TimerModifier.class, TimerModifier::ticksPlayed, -1L);
        collectedItems.add(CollectedItem.now(currentItem, collector.getName(), ticksPlayed, false));
        notifyItemsCollected(collector, currentItem, false);
        nextItem();
    }

    public void skipCurrentItem(CommandSender collector) {
        if (allItemsCollected) return;
        long ticksPlayed = getChallenge().modifier(TimerModifier.class, TimerModifier::ticksPlayed, -1L);
        collectedItems.add(CollectedItem.now(currentItem, collector.getName(), ticksPlayed, true));
        notifyItemsCollected(collector, currentItem, true);
        nextItem();
    }

    private void nextItem() {
        List<AllItem> remainingItems = remainingItems();
        if (remainingItems.isEmpty()) {
            allItemsCollected = true;
        } else {
            currentItem = remainingItems.get(RandomHolder.random().nextInt(remainingItems.size()));
            broadcastNextItem(currentItem);
            checkAllPlayerInventories();
        }
        currentDisplay = null;
    }

    public void reset() {
        this.collectedItems.clear();
        this.allItemsCollected = false;
        currentItem = EnumUtils.random(AllItem.class);
        currentDisplay = null;
    }

    private List<AllItem> remainingItems() {
        EnumSet<AllItem> alreadyCollected = Sets.newEnumSet(collectedItems.stream().map(CollectedItem::item).toList(),
                AllItem.class);

        return EnumUtils.asStream(AllItem.class)
                .filter(Predicate.not(alreadyCollected::contains))
                .toList();
    }

    private Component itemDisplay() {
        if (currentDisplay != null) return currentDisplay;

        int collectedItemCount = collectedItems.size();
        int itemsToCollectCount = AllItem.values().length;

        Option<Component> component =
                allItemsCollected ? Option.AllItems.BossBar.COMPLETE : Option.AllItems.BossBar.NEXT_ITEM;

        currentDisplay = component.resolve(
                Placeholder.component("item_icon", currentItem.icon()),
                Placeholder.component("item_name", currentItem.itemName()),
                Formatter.number("items_collected", collectedItemCount),
                Formatter.number("total_items", itemsToCollectCount),
                Formatter.number("progress", (float) collectedItemCount / itemsToCollectCount * 100)
        );
        return currentDisplay;
    }

    private void checkAllPlayerInventories() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            var inventory = player.getInventory();

            Optional<ItemStack> foundItem = Stream.of(inventory.getContents(), inventory.getArmorContents())
                    .flatMap(Stream::of)
                    .filter(itemStack -> currentItem.matches(itemStack))
                    .findAny();

            if (foundItem.isPresent()) {
                collectCurrentItem(player);
                break;
            }
        }
    }

    private static void notifyItemsCollected(CommandSender collector, AllItem item, boolean skipped) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1, 1));

        UUID senderUUId = collector instanceof Player player ? player.getUniqueId() : null;

        Option<Component> component = skipped ? Option.AllItems.Chat.ITEM_SKIPPED : Option.AllItems.Chat.ITEM_COLLECTED;
        if (skipped && senderUUId == null) {
            component = Option.AllItems.Chat.ITEM_SKIPPED_CONSOLE;
        }

        Bukkit.broadcast(component.resolve(
                Placeholder.parsed("player_head", "<head:" + senderUUId + ":true>"),
                Placeholder.component("player", collector.name()),
                Placeholder.component("item_name", item.itemName())
        ));
    }

    private static void broadcastNextItem(AllItem item) {
        var component = Option.AllItems.Chat.NEXT_ITEM.resolve(Placeholder.component("item_name", item.itemName()));
        Bukkit.broadcast(component);
    }

}
