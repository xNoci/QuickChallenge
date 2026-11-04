package me.noci.challenges.challenge.modifiers.randomizer;

import com.google.common.collect.Maps;
import net.kyori.adventure.key.Key;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;

import java.util.Arrays;
import java.util.Map;
import java.util.random.RandomGenerator;

public class LootTableRandomizerModifier extends RandomizerModifier<Key, LootTable> {

    private static final LootTables[] LOOT_TABLES = Arrays.stream(LootTables.values())
            .filter(lt -> lt.getKey().getKey().startsWith("chests/"))
            .toArray(LootTables[]::new);

    private static final Map<Key, LootTables> LOOT_TABLE_MAP = Maps.newHashMap();

    static {
        for (LootTables lootTable : LOOT_TABLES) {
            LOOT_TABLE_MAP.put(lootTable.key(), lootTable);
        }
    }

    public LootTableRandomizerModifier() {
        super("LootTable Randomizer", Maps::newHashMap);
    }

    @Override
    protected LootTable randomize(Long seed, Key value, RandomProvider generator) {
        if (!LOOT_TABLE_MAP.containsKey(value)) {
            throw new IllegalStateException("Invalid loot table key: " + value + " - only structures are supported");
        }
        LootTables lootTable = LOOT_TABLE_MAP.get(value);

        RandomGenerator randomGenerator = generator.create(seed + lootTable.ordinal());
        return LOOT_TABLES[randomGenerator.nextInt(LOOT_TABLES.length)].getLootTable();
    }
}
