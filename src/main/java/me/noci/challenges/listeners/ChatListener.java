package me.noci.challenges.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.noci.challenges.headcomponent.HeadComponent;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final Config config;

    public ChatListener(Config config) {
        this.config = config;
    }

    @EventHandler
    public void handleChatDecorate(AsyncChatEvent event) {
        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> {
                    TagResolver resolver = TagResolver.builder()
                            .resolvers(
                                    Placeholder.component("player_head", HeadComponent.create(source.getUniqueId()).build()),
                                    Placeholder.component("player", sourceDisplayName),
                                    Placeholder.component("message", message)
                            )
                            .build();
                    return config.get(Option.CHAT_LAYOUT, resolver);
                }
        ));
    }
}