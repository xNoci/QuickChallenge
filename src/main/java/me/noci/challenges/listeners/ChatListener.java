package me.noci.challenges.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.noci.challenges.colors.Colors;
import me.noci.challenges.headcomponent.HeadComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    @EventHandler
    public void handleChatDecorate(AsyncChatEvent event) {
        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> Component.empty()
                .append(HeadComponent.create(source.getUniqueId()).build())
                .append(Component.space())
                .append(sourceDisplayName.color(Colors.PLAYER_NAME))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(message.color(NamedTextColor.GRAY))
        ));
    }
}