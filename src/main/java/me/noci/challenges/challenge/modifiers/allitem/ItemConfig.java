package me.noci.challenges.challenge.modifiers.allitem;

import com.google.common.collect.Maps;
import io.leangen.geantyref.TypeFactory;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@ConfigSerializable
public class ItemConfig {

    private static final Logger LOGGER = LogManager.getLogger("ItemConfig");
    private static final Type MAP_TYPE = TypeFactory.parameterizedClass(Map.class, String.class, ItemConfig.class);

    @Getter
    @Setting("icon")
    private char icon = 'x';

    @Setting("material")
    private Material material;

    @Setting("potion")
    private PotionType potion;

    @Getter
    @Setting("ominous_banner")
    private boolean ominousBanner;

    @Setting("abandoned_camp")
    private String abandonedCamp;

    public Optional<Material> material() {
        return Optional.ofNullable(material);
    }

    public Optional<PotionType> potion() {
        return Optional.ofNullable(potion);
    }

    public Optional<String> abandonedCamp() {
        return Optional.ofNullable(abandonedCamp);
    }

    @SuppressWarnings("unchecked")
    protected static Map<String, ItemConfig> loadItems(Path path) {
        ConfigurationLoader<?> loader = YamlConfigurationLoader.builder().path(path).build();

        try {
            ConfigurationNode root = loader.load();
            Map<String, ItemConfig> items = (Map<String, ItemConfig>) root.get(MAP_TYPE);

            return items != null ? items : Maps.newHashMap();
        } catch (ConfigurateException e) {
            LOGGER.error("Failed to load items.", e);
            return Maps.newHashMap();
        }
    }
}
