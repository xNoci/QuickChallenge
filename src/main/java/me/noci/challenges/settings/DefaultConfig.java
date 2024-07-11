package me.noci.challenges.settings;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.SneakyThrows;
import me.noci.quickutilities.utils.Require;
import me.noci.quickutilities.utils.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class DefaultConfig implements Config {

    private final List<Runnable> reloadListeners = Lists.newArrayList();
    private final Logger logger;
    private final JavaPlugin plugin;
    private final Path config;

    @Getter private YamlConfiguration configuration;

    protected DefaultConfig(JavaPlugin plugin, String fileName, boolean replace) {
        Require.nonNull(plugin, "JavaPlugin cannot be null");
        Require.checkArgument(!StringUtils.isBlank(fileName), "File name cannot be null or empty");

        this.logger = LogManager.getLogger("ConfigWatcher (" + fileName + ")");
        this.plugin = plugin;

        config = createDefault(fileName, replace);
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
            case Component c -> get((Option<Component>) option, TagResolver.empty());
            case TextColor c -> parseTextColor(path, c);
            default -> configuration.get(option.path(), option.defaultValue());
        };
    }

    @Override
    public Component get(Option<Component> option, TagResolver resolver) {
        String string = configuration.getString(option.path());
        return string == null ? option.defaultValue() : MiniMessage.miniMessage().deserialize(string, resolver);
    }

    @Override
    public void registerListener(Runnable listener) {
        Require.checkState(!reloadListeners.contains(listener), "Listener is already registered");
        reloadListeners.add(listener);
    }

    @Override
    public void removeListener(Runnable listener) {
        Require.checkState(reloadListeners.contains(listener), "This listener is not registered");
        reloadListeners.remove(listener);
    }

    private TextColor parseTextColor(String path, TextColor defaultValue) {
        String color = configuration.getString(path);
        if (color == null || color.isBlank()) return defaultValue;

        TextColor textColor;

        if (color.charAt(0) == TextColor.HEX_CHARACTER) {
            textColor = TextColor.fromHexString(color);
        } else {
            textColor = NamedTextColor.NAMES.value(color.toLowerCase());
        }

        return textColor != null ? textColor : defaultValue;
    }

    protected void load() {
        this.configuration = YamlConfiguration.loadConfiguration(config.toFile());
        reloadListeners.forEach(Runnable::run);
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

    protected void listenForChanges() {
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
                        load();
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
