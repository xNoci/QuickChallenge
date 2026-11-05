package me.noci.challenges.challenge.modifiers.allitem;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.noci.challenges.ResourcePack;
import me.noci.challenges.challenge.modifiers.allitem.custommatcher.ItemMatcher;
import me.noci.challenges.challenge.modifiers.allitem.custommatcher.OminousBannerMatcher;
import me.noci.challenges.challenge.modifiers.allitem.custommatcher.PotionMatcher;
import me.noci.quickutilities.utils.ItemBuilder;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllItemRegistry {

    private static final Logger LOGGER = LogManager.getLogger(AllItemRegistry.class.getSimpleName());
    private static final Map<Key, AllItem> keyedItems = Maps.newHashMap();
    private static final List<AllItem> items = Lists.newArrayList();

    public static void loadItems(Path path) {
        Map<String, ItemConfig> items = ItemConfig.loadItems(path);

        for (Map.Entry<String, ItemConfig> entry : items.entrySet()) {
            Key key = Key.key("allitem" + Key.DEFAULT_SEPARATOR + entry.getKey(), Key.DEFAULT_SEPARATOR);
            ItemConfig config = entry.getValue();

            ItemMatcher matcher;
            if (config.ominousBanner()) {
                matcher = new OminousBannerMatcher();
            } else if (config.potion().isPresent()) {
                matcher = new PotionMatcher(config.material().orElse(Material.POTION), config.potion().get());
            } else {
                matcher = new ItemMatcher(
                        config.material().orElseThrow(() -> new IllegalStateException("Material is missing.")));
            }

            AllItem item = new AllItem(key, config.icon(), matcher);
            register(item);
        }

    }

    private static void register(AllItem item) {
        Require.checkState(!keyedItems.containsKey(item.key()), "Item with key " + item.key() + " is already registered");
        keyedItems.put(item.key(), item);
        items.add(item);
        LOGGER.info("Registered new item for key '{}' with matcher '{}'", item.key().asString(), item.matcher().getClass().getSimpleName());
    }

    public static AllItem random() {
        return items.get((int) (Math.random() * items.size()));
    }

    public static int totalItemCount() {
        return items.size();
    }

    public static List<AllItem> all() {
        return new ArrayList<>(items);
    }

    public static AllItem fromKey(Key key) {
        return keyedItems.get(key);
    }

}
