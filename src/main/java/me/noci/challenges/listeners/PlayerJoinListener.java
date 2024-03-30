package me.noci.challenges.listeners;

import me.noci.challenges.ResourcePack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Component component = Component.text()
                .append(Component.text("» ", TextColor.color(78, 82, 79)))
                .append(Component.text(player.getName(), TextColor.color(31, 150, 173)))
                .append(Component.text(" joined the server", TextColor.color(255, 255, 255)))
                .asComponent();

        event.joinMessage(component);

        player.setResourcePack(ResourcePack.URL, ResourcePack.HASH, ResourcePack.REQUIRED, ResourcePack.PROMPT);
        
    }

}
