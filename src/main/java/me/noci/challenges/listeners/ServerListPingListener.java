package me.noci.challenges.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerListPingListener implements Listener {

    private static final TagResolver VERSION_RESOLVER = Placeholder.parsed("version", Bukkit.getMinecraftVersion());

    private final Component motd;

    public ServerListPingListener(FileConfiguration config) {
        String configMotd = config.getString("settings.motd", "<bold>Challenges</bold>");
        this.motd = MiniMessage.miniMessage().deserialize(configMotd, VERSION_RESOLVER);
    }

    @EventHandler
    public void handleServerListPing(PaperServerListPingEvent event) {
        event.motd(motd);
    }

}
