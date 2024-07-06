package me.noci.challenges.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerListPingListener implements Listener {

    private final Config config;

    public ServerListPingListener(Config config) {
        this.config = config;
    }

    @EventHandler
    public void handleServerListPing(PaperServerListPingEvent event) {
        TagResolver resolver = TagResolver.builder()
                .resolvers(
                        Placeholder.unparsed("version", Bukkit.getMinecraftVersion()),
                        Formatter.number("day", Bukkit.getWorlds().getFirst().getFullTime() / 24000)
                )
                .build();

        event.motd(config.get(Option.Settings.MOTD, resolver));
    }

}
