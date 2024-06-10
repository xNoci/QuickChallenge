package me.noci.challenges.settings;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.SneakyThrows;
import me.noci.quickutilities.utils.Require;
import me.noci.quickutilities.utils.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentDecoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;

public class DefaultConfig implements Config {

    private final Logger logger;
    private final JavaPlugin plugin;
    private final Path config;

    @Getter private YamlConfiguration configuration;
    private final HashMap<String, Component> componentCache = Maps.newHashMap();

    public DefaultConfig(JavaPlugin plugin, String fileName, boolean replace, boolean autoUpdate) {
        Require.nonNull(plugin, "JavaPlugin cannot be null");
        Require.checkArgument(!StringUtils.isBlank(fileName), "File name cannot be null or empty");

        this.logger = LogManager.getLogger("ConfigWatcher (" + fileName + ")");
        this.plugin = plugin;

        config = createDefault(fileName, replace);

        reload();

        if (autoUpdate) {
            listenForChanges();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Option<T> option) {
        String path = option.path();
        T def = option.defaultValue();

        return (T) switch (def) {
            case Integer i -> configuration.getInt(path, i);
            case String s -> configuration.getString(path, s);
            case Boolean b -> configuration.getBoolean(path, b);
            case Float f -> configuration.getDouble(path, (double) f);
            case Double d -> configuration.getDouble(path, d);
            case Component c -> cachedComponent(path, MiniMessage.miniMessage(), c);
            default -> configuration.get(option.path(), option.defaultValue());
        };
    }

    @Override
    public Component get(Option<Component> option, ComponentDecoder<? super String, Component> decoder) {
        return cachedComponent(option.path(), decoder, option.defaultValue());
    }

    public Component cachedComponent(@NotNull String path, ComponentDecoder<? super String, Component> decoder, Component fallback) {
        return componentCache.computeIfAbsent(path, key -> configuration.getComponent(key, decoder, fallback));
    }

    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(config.toFile());
        componentCache.clear();
    }

    @SneakyThrows
    private Path createDefault(String fileName, boolean replace) {
        Path dataFolder = plugin.getDataFolder().toPath();

        Files.createDirectories(dataFolder);
        Path targetFile = dataFolder.resolve(fileName);

        if (Files.exists(targetFile) && !replace) return targetFile;

        try (var inputStream = plugin.getResource(fileName)) {
            if (inputStream == null) {
                throw new IllegalStateException("Could not find ressource file '" + fileName + "'");
            }
            Files.copy(inputStream, targetFile);
            return targetFile;
        }
    }

    private void listenForChanges() {
        Scheduler.executeAsync(() -> {
            try (WatchService service = FileSystems.getDefault().newWatchService()) {
                Path parentDirectory = config.getParent();
                if (parentDirectory == null || !Files.isDirectory(parentDirectory)) {
                    throw new IllegalStateException("Could not watch file '%s': Parent is not a directory".formatted(config.getFileName().toString()));
                }

                parentDirectory.register(service, StandardWatchEventKinds.ENTRY_MODIFY);

                logger.info("Start watching config");

                while (plugin.isEnabled()) {
                    WatchKey key = service.poll();
                    if (key == null) continue;

                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path updatedFile = (Path) event.context();
                        if (!config.endsWith(updatedFile)) continue;
                        reload();
                    }

                    key.reset();
                }

                logger.info("Stopped watching config file");

            } catch (IOException e) {
                logger.info("Failed to watch config: ", e.fillInStackTrace());
            }
        });
    }

}
