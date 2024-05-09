package me.noci.challenges.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.noci.challenges.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerListPingListener implements Listener {

    private static final Component DEFAULT_MOTD = Component.text("                  Challenges").decorate(TextDecoration.BOLD);
    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(StandardTags.defaults()).editTags(builder -> builder.resolver(Placeholder.parsed("version", Bukkit.getMinecraftVersion()))).build();

    private final Config config;

    public ServerListPingListener(Config config) {
        this.config = config;
    }

    @EventHandler
    public void handleServerListPing(PaperServerListPingEvent event) {
        event.motd(config.cachedComponent("settings.motd", MINI_MESSAGE, DEFAULT_MOTD));
    }

}
