package me.noci.challenges.challenge.serializ;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.noci.challenges.ExitStrategy;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.*;
import me.noci.challenges.serializer.ObjectSerializer;
import me.noci.challenges.serializer.TypeSerializers;
import me.noci.challenges.worlds.ChallengeLocation;
import me.noci.challenges.worlds.RespawnLocation;
import me.noci.quickutilities.utils.Require;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ChallengeSerializer {

    private static final Logger LOGGER = LogManager.getLogger("Challenge Serializer");
    private static final HashMap<Integer, ObjectSerializer<Challenge>> SERIALIZERS = Maps.newHashMap();
    private static final short MAGIC_NUMBER = (short) 0xFE21;
    private static final int CURRENT_VERSION = 5;

    static {
        register(5,
                serializer -> serializer.layout(TypeSerializers.UUID, Challenge::handle)
                        .layout(TypeSerializers.EXIT_STRATEGY, Challenge::exitStrategy)
                        .layout(TypeSerializers.CHALLENGE_LOCATION_MAP, Challenge::lastKnownLocation)
                        .layout(TypeSerializers.RESPAWN_LOCATION_MAP, Challenge::respawnLocations)
                        .layout(StopOnDeathModifier.SERIALIZER, challenge -> challenge.modifier(StopOnDeathModifier.class))
                        .layout(TimerModifier.SERIALIZER, challenge -> challenge.modifier(TimerModifier.class))
                        .layout(TrafficLightModifier.SERIALIZER, challenge -> challenge.modifier(TrafficLightModifier.class))
                        .layout(EnderDragonFinishModifier.SERIALIZER, challenge -> challenge.modifier(EnderDragonFinishModifier.class)),
                buffer -> {
                    UUID uuid = TypeSerializers.UUID.read(buffer);
                    ExitStrategy exitStrategy = TypeSerializers.EXIT_STRATEGY.read(buffer);
                    Map<UUID, ChallengeLocation> lastKnownLocations = TypeSerializers.CHALLENGE_LOCATION_MAP.read(buffer);
                    Map<UUID, RespawnLocation> respawnLocations = TypeSerializers.RESPAWN_LOCATION_MAP.read(buffer);
                    Optional<StopOnDeathModifier> stopOnDeathModifier = StopOnDeathModifier.SERIALIZER.read(buffer);
                    Optional<TimerModifier> timerModifier = TimerModifier.SERIALIZER.read(buffer);
                    Optional<TrafficLightModifier> trafficLightModifier = TrafficLightModifier.SERIALIZER.read(buffer);
                    Optional<EnderDragonFinishModifier> enderDragonFinishModifier = EnderDragonFinishModifier.SERIALIZER.read(buffer);

                    List<ChallengeModifier> challengeModifiers = Lists.newArrayList();
                    stopOnDeathModifier.ifPresent(challengeModifiers::add);
                    timerModifier.ifPresent(challengeModifiers::add);
                    trafficLightModifier.ifPresent(challengeModifiers::add);
                    enderDragonFinishModifier.ifPresent(challengeModifiers::add);

                    return new Challenge(uuid, exitStrategy, lastKnownLocations, respawnLocations, challengeModifiers);
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
