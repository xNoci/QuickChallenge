package me.noci.challenges.challenge.modifiers.randomizer;

import com.google.common.collect.Maps;
import me.noci.challenges.challenge.modifiers.registry.ModifierRegistry;
import net.kyori.adventure.key.Key;

import java.util.Map;
import java.util.function.Supplier;

public class RandomizerRegistry {

    private static final Map<Key, Supplier<RandomizerModifier<?, ?>>> REGISTRY = Maps.newHashMap();

    static {
        REGISTRY.put(ModifierRegistry.BLOCK_RANDOMIZER, BlockRandomizerModifier::new);
        REGISTRY.put(ModifierRegistry.MOB_DROP_RANDOMIZER, MobDropRandomizerModifier::new);
    }

    public static RandomizerModifier<?, ?> createRandomizer(Key key) {
        if (!REGISTRY.containsKey(key)) {
            throw new IllegalStateException("Unknown randomizer type: " + key);
        }

        return REGISTRY.get(key).get();
    }

}
