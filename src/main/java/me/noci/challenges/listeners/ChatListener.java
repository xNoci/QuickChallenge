package me.noci.challenges.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.noci.challenges.headcomponent.HeadComponent;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    @EventHandler
    public void handleChatDecorate(AsyncChatEvent event) {
        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> Option.Settings.CHAT_LAYOUT.resolve(
                Placeholder.component("player_head", HeadComponent.create(source.getUniqueId()).build()),
                Placeholder.component("player", sourceDisplayName),
                Placeholder.component("message", message))
        ));
    }
}