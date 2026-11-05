package me.noci.challenges.serializer;

import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

public class ChallengeLoader {

    private static final TypeSerializerCollection DEFAULTS;


    static {
        DEFAULTS = TypeSerializerCollection.builder()
                .registerExact(AllItemSerializer.TYPE, AllItemSerializer.INSTANCE)
                .registerExact(ChallengeSerializer.TYPE, ChallengeSerializer.INSTANCE)
                .registerExact(EnderDragonFinishSerializer.TYPE, EnderDragonFinishSerializer.INSTANCE)
                .registerExact(StopOnDeathSerializer.TYPE, StopOnDeathSerializer.INSTANCE)
                .registerExact(TrafficLightSerializer.TYPE, TrafficLightSerializer.INSTANCE)
                .registerExact(TimerSerializer.TYPE, TimerSerializer.INSTANCE)
                .register(RandomizerSerializer.TYPE, RandomizerSerializer.INSTANCE)
                .registerAll(ConfigurateComponentSerializer.configurate().serializers())
                .build();
    }

    public static ConfigurationLoader<?> createLoader(Path path) {
        return YamlConfigurationLoader.builder()
                .path(path)
                .defaultOptions(opts -> opts.serializers(build -> build.registerAll(DEFAULTS)))
                .build();
    }

}
