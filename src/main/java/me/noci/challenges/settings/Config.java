package me.noci.challenges.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentDecoder;
import org.bukkit.plugin.java.JavaPlugin;

public interface Config {

    static Config create(JavaPlugin plugin, String fileName, boolean replace, boolean autoUpdate) {
        var config = new DefaultConfig(plugin, fileName, replace);

        config.load();

        if (autoUpdate) {
            config.listenForChanges();
        }

        return config;
    }

    <T> T get(Option<T> option);

    Component get(Option<Component> option, ComponentDecoder<? super String, Component> decoder);

}
