package me.noci.challenges.serializer;

import com.google.common.collect.Maps;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.EnderDragonFinishModifier;
import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.registry.ModifierHolder;
import me.noci.challenges.challenge.modifiers.registry.ModifierRegistry;
import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.challenges.serializer.utils.Node;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Map;

public final class ChallengeSerializer implements TypeSerializer<Challenge> {

    static final ChallengeSerializer INSTANCE = new ChallengeSerializer();
    static final Class<Challenge> TYPE = Challenge.class;

    private static final String CREATOR = "created-by";
    private static final String CREATED_AT = "created-at";
    private static final String MODIFIERS = "modifiers";

    private ChallengeSerializer() {

    }

    @Override
    public Challenge deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String creator = Node.nonVirtual(source, CREATOR).getString();
        long createdAt = Node.nonVirtual(source, CREATED_AT).getLong();
        ConfigurationNode modifierNodes = Node.nonVirtual(source, MODIFIERS);

        Map<Class<? extends ChallengeModifier>, ChallengeModifier> modifiers = Maps.newHashMap();

        for (ModifierHolder<? extends ChallengeModifier> holder : ModifierRegistry.modifiers()) {
            String key = holder.key();

            if (modifierNodes.hasChild(key)) {
                ChallengeModifier modifierInstance = Node.nonVirtual(modifierNodes, key).get(holder.type());
                modifiers.put(holder.type(), modifierInstance);
            }
        }

        return new Challenge(creator, createdAt, modifiers);
    }

    @Override
    public void serialize(Type type, @org.jspecify.annotations.Nullable Challenge obj, ConfigurationNode target)
            throws SerializationException {
        if (obj == null) {
            target.raw(null);
            return;
        }

        target.node(CREATOR).set(obj.createdBy());
        target.node(CREATED_AT).set(obj.createTimestamp());
        ConfigurationNode modifierNodes = target.node(MODIFIERS);

        for (ChallengeModifier modifier : obj.modifiers()) {
            modifierNodes.node(modifier.getClass().getSimpleName()).set(modifier.getClass(), modifier);
        }
    }
}
