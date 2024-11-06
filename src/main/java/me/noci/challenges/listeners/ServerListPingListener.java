package me.noci.challenges.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerListPingListener implements Listener {

    @EventHandler
    public void handleServerListPing(PaperServerListPingEvent event) {
        event.motd(Option.Settings.MOTD.resolve(
                Placeholder.unparsed("version", Bukkit.getMinecraftVersion()),
                Formatter.number("day", Bukkit.getWorlds().getFirst().getFullTime() / 24000)
        ));
    }

}
