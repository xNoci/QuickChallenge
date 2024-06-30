package me.noci.challenges.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
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
        TagResolver[] resolvers = new TagResolver[]{
                Placeholder.unparsed("version", Bukkit.getMinecraftVersion()),
                Formatter.number("day", Bukkit.getWorlds().getFirst().getFullTime() / 24000)
        };

        MiniMessage decoder = MiniMessage.builder()
                .tags(StandardTags.defaults())
                .editTags(builder -> builder.resolvers(resolvers))
                .build();

        event.motd(config.get(Option.MOTD, decoder));
    }

}
