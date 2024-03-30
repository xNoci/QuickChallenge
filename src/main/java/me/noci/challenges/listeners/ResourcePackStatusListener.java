package me.noci.challenges.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;


public class ResourcePackStatusListener implements Listener {

    private static final Component RESOURCE_PACK_WARNING = Component.text()
            .append(Component.text("You are currently playing without the server resource pack. Please make sure to enable it, when playing a challenge.", TextColor.color(82, 12, 7)))
            .asComponent();

    @EventHandler
    public void handleResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event.getStatus() != PlayerResourcePackStatusEvent.Status.DECLINED) return;
        event.getPlayer().sendMessage(RESOURCE_PACK_WARNING);
    }

}
