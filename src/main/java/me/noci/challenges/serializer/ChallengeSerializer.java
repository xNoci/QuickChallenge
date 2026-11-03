package me.noci.challenges.serializer;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.registry.ModifierHolder;
import me.noci.challenges.challenge.modifiers.registry.ModifierRegistry;
import me.noci.challenges.serializer.utils.Node;
import net.kyori.adventure.key.Key;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

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

        Set<ChallengeModifier> modifiers = new HashSet<>();

        for (ModifierHolder<? extends ChallengeModifier> holder : ModifierRegistry.modifiers()) {
            String key = holder.key().asString();

            if (modifierNodes.hasChild(key)) {
                ChallengeModifier modifierInstance = Node.nonVirtual(modifierNodes, key).get(holder.type());
                modifiers.add(modifierInstance);
            }
        }

        return new Challenge(creator, createdAt, modifiers);
    }

    @Override
    public void serialize(Type type, Challenge obj, ConfigurationNode target)
            throws SerializationException {
        if (obj == null) {
            target.raw(null);
            return;
        }

        target.node(CREATOR).set(obj.createdBy());
        target.node(CREATED_AT).set(obj.createTimestamp());
        ConfigurationNode modifierNodes = target.node(MODIFIERS);

        for (ChallengeModifier modifier : obj.modifiers()) {
            Key key = ModifierRegistry.fromType(modifier.getClass()).key();
            modifierNodes.node(key.asString()).set(modifier.getClass(), modifier);
        }
    }
}
