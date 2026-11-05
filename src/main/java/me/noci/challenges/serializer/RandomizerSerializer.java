package me.noci.challenges.serializer;


import com.google.common.collect.Maps;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;
import me.noci.challenges.challenge.modifiers.randomizer.RandomizerModifier;
import me.noci.challenges.challenge.modifiers.randomizer.RandomizerRegistry;
import me.noci.challenges.challenge.modifiers.registry.ModifierRegistry;
import me.noci.challenges.serializer.utils.Node;
import net.kyori.adventure.key.Key;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;


public final class RandomizerSerializer implements TypeSerializer<RandomizerModifier<?, ?>> {

    static final RandomizerSerializer INSTANCE = new RandomizerSerializer();
    public static final TypeToken<RandomizerModifier<?, ?>> TYPE = new TypeToken<>() {
    };

    private static final Type SEED_MAP_TYPE = TypeFactory.parameterizedClass(Map.class, UUID.class, Long.class);

    private static final String RANDOMIZER_TYPE = "type";
    private static final String SEEDS = "seeds";

    private RandomizerSerializer() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public RandomizerModifier<?, ?> deserialize(Type type, ConfigurationNode source) throws SerializationException {
        Map<UUID, Long> seeds = (Map<UUID, Long>) Node.nonVirtual(source, SEEDS).get(SEED_MAP_TYPE);

        if (seeds == null) {
            seeds = Maps.newHashMap();
        }

        Key key = Node.nonVirtual(source, RANDOMIZER_TYPE).get(Key.class);
        RandomizerModifier<?, ?> modifier = RandomizerRegistry.createRandomizer(key);
        if (modifier == null) {
            throw new SerializationException("Unknown randomizer type: " + key);
        }
        modifier.setSeeds(seeds);

        return modifier;
    }

    @Override
    public void serialize(Type type, RandomizerModifier<?, ?> obj, ConfigurationNode target)
            throws SerializationException {
        if (obj == null) {
            target.raw(null);
            return;
        }

        Key key = ModifierRegistry.fromType(obj.getClass()).key();
        target.node(RANDOMIZER_TYPE).set(Key.class, key);
        target.node(SEEDS).set(SEED_MAP_TYPE, obj.seeds());
    }

}
