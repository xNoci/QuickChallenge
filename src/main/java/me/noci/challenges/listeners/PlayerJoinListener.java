package me.noci.challenges.listeners;

import me.noci.challenges.colors.Colors;
import me.noci.challenges.ResourcePack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Component component = Component.text()
                .append(Component.text("» ", Colors.JOIN_INDICATOR_JOIN))
                .append(Component.text(player.getName(), Colors.JOIN_PLAYER_NAME))
                .append(Component.text(" joined the server", Colors.CHAT_COLOR))
                .asComponent();

        event.joinMessage(component);

        player.setResourcePack(ResourcePack.URL, ResourcePack.HASH, !player.isOp() && ResourcePack.REQUIRED, ResourcePack.PROMPT);
    }


}
