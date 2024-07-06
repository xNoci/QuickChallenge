package me.noci.challenges.challenge.serializ;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.EnderDragonFinishModifier;
import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import me.noci.challenges.challenge.modifiers.TimerModifier;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.challenges.serializer.ObjectSerializer;
import me.noci.challenges.serializer.TypeSerializers;
import me.noci.quickutilities.utils.Require;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ChallengeSerializer {

    private static final Logger LOGGER = LogManager.getLogger("Challenge Serializer");
    private static final HashMap<Integer, ObjectSerializer<Challenge>> SERIALIZERS = Maps.newHashMap();
    private static final short MAGIC_NUMBER = (short) 0xFE21;
    private static final int CURRENT_VERSION = 3;

    static {
        register(1,
                serializer -> serializer
                        .layout(StopOnDeathSerializer.SERIALIZER, challenge -> challenge.modifier(StopOnDeathModifier.class))
                        .layout(TimerSerializer.SERIALIZER, challenge -> challenge.modifier(TimerModifier.class))
                        .layout(TrafficLightSerializer.SERIALIZER, challenge -> challenge.modifier(TrafficLightModifier.class))
                        .layout(EnderDragonFinishSerializer.SERIALIZER, challenge -> challenge.modifier(EnderDragonFinishModifier.class))
                        .layout(AllItemSerializer.V_1, challenge -> challenge.modifier(AllItemModifier.class)),
                buffer -> {
                    List<ChallengeModifier> challengeModifiers = Lists.newArrayList();
                    StopOnDeathSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    TimerSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    TrafficLightSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    EnderDragonFinishSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    AllItemSerializer.V_1.read(buffer).ifPresent(challengeModifiers::add);

                    return new Challenge(challengeModifiers);
                }
        );

        register(2,
                serializer -> serializer
                        .layout(StopOnDeathSerializer.SERIALIZER, challenge -> challenge.modifier(StopOnDeathModifier.class))
                        .layout(TimerSerializer.SERIALIZER, challenge -> challenge.modifier(TimerModifier.class))
                        .layout(TrafficLightSerializer.SERIALIZER, challenge -> challenge.modifier(TrafficLightModifier.class))
                        .layout(EnderDragonFinishSerializer.SERIALIZER, challenge -> challenge.modifier(EnderDragonFinishModifier.class))
                        .layout(AllItemSerializer.V_2, challenge -> challenge.modifier(AllItemModifier.class)),

                buffer -> {
                    List<ChallengeModifier> challengeModifiers = Lists.newArrayList();
                    StopOnDeathSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    TimerSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    TrafficLightSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    EnderDragonFinishSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    AllItemSerializer.V_2.read(buffer).ifPresent(challengeModifiers::add);

                    return new Challenge(challengeModifiers);
                }
        );

        register(3,
                serializer -> serializer
                        .layout(StopOnDeathSerializer.SERIALIZER, challenge -> challenge.modifier(StopOnDeathModifier.class))
                        .layout(TimerSerializer.SERIALIZER, challenge -> challenge.modifier(TimerModifier.class))
                        .layout(TrafficLightSerializer.SERIALIZER, challenge -> challenge.modifier(TrafficLightModifier.class))
                        .layout(EnderDragonFinishSerializer.SERIALIZER, challenge -> challenge.modifier(EnderDragonFinishModifier.class))
                        .layout(AllItemSerializer.V_3, challenge -> challenge.modifier(AllItemModifier.class)),

                buffer -> {
                    List<ChallengeModifier> challengeModifiers = Lists.newArrayList();
                    StopOnDeathSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    TimerSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    TrafficLightSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    EnderDragonFinishSerializer.SERIALIZER.read(buffer).ifPresent(challengeModifiers::add);
                    AllItemSerializer.V_3.read(buffer).ifPresent(challengeModifiers::add);

                    return new Challenge(challengeModifiers);
                }
        );

    }

    private static void register(int version, Consumer<ObjectSerializer.Builder<Challenge>> layout, ObjectSerializer.ByteReader<Challenge> reader) {
        Require.checkState(!SERIALIZERS.containsKey(version), "A challenge serializer for version '%s' already exists.", version);
        var builder = ObjectSerializer.of(Challenge.class)
                .layout(TypeSerializers.SHORT, c -> MAGIC_NUMBER)
                .layout(TypeSerializers.U_BYTE, c -> version);

        layout.accept(builder);

        builder.read(buffer -> {
            if (TypeSerializers.SHORT.read(buffer) != MAGIC_NUMBER) return null;
            if (TypeSerializers.U_BYTE.read(buffer) != version) return null;
            return reader.read(buffer);
        });

        SERIALIZERS.put(version, builder.get());
    }


    public static Optional<Challenge> read(byte[] data) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            if (TypeSerializers.SHORT.read(buffer) != MAGIC_NUMBER) return Optional.empty();
            int version = TypeSerializers.U_BYTE.read(buffer);


            if (!SERIALIZERS.containsKey(version)) {
                LOGGER.info("Could not find a challenge serializer for version: {}", version);
                return Optional.empty();
            }

            buffer.rewind();
            return SERIALIZERS.get(version).read(buffer);
        } catch (Exception e) {
            LOGGER.error("Failed to serialize challenge data: ", e);
            return Optional.empty();
        }
    }

    public static byte[] serialize(Challenge challenge) {
        return SERIALIZERS.get(CURRENT_VERSION).serialize(challenge);
    }
}
