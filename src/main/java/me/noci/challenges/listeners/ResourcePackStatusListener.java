package me.noci.challenges.listeners;

import me.noci.challenges.ResourcePack;
import me.noci.challenges.colors.Colors;
import me.noci.challenges.headcomponent.HeadComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;


public class ResourcePackStatusListener implements Listener {

    private static final Component RESOURCE_PACK_WARNING = Component.text()
            .append(Component.text("You are currently playing without the server resource pack. Please make sure to enable it, when playing a challenge.", TextColor.color(82, 12, 7)))
            .asComponent();

    @EventHandler
    public void handleResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (!ResourcePack.DEFAULT.isSame(event.getID())) return;

        Player player = event.getPlayer();

        Component joinMessage = Component.text()
                .append(Component.text("» ", Colors.JOIN_INDICATOR_JOIN))
                .append(HeadComponent.create(player.getUniqueId()).build())
                .append(Component.space())
                .append(Component.text(player.getName(), Colors.PLAYER_NAME))
                .append(Component.text(" joined the server", Colors.CHAT_COLOR))
                .asComponent();

        switch (event.getStatus()) {
            case DECLINED:
                event.getPlayer().sendMessage(RESOURCE_PACK_WARNING);
            case SUCCESSFULLY_LOADED:
                Bukkit.broadcast(joinMessage);
        }

    }

}
