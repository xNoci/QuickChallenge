package me.noci.challenges.paper;

import lombok.Getter;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.allitem.AllItemRegistry;
import me.noci.challenges.listeners.*;
import me.noci.challenges.listeners.modifiers.*;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.function.Consumer;

public class QuickChallenge extends JavaPlugin {

    @Getter
    private static QuickChallenge instance;

    @Getter
    private Config config;
    @Getter
    private final ChallengeController challengeController;

    public QuickChallenge(ChallengeController challengeController) {
        instance = this;
        this.challengeController = challengeController;
    }

    @Override
    public void onEnable() {
        Path allItemsFile = getDataPath().resolve("all_items.yml");
        AllItemRegistry.loadItems(this, "all_items.yml", allItemsFile);

        this.config = Config.create(this, "config.yml", false, true);
        this.challengeController.tryLoadChallenge();

        registerListeners();
    }

    @Override
    public void onDisable() {
        Component kickComponent = Option.Settings.SERVER_CLOSED.get();
        this.challengeController.stopChallenge();
        this.challengeController.save();
        Bukkit.getOnlinePlayers().forEach(player -> player.kick(kickComponent));
    }

    private void registerListeners() {
        Consumer<Listener> register = listener -> getServer().getPluginManager().registerEvents(listener, this);

        // Challenge
        register.accept(new ChallengeMasterListener(challengeController));

        // Modifiers
        register.accept(new AllItemListener(challengeController));
        register.accept(new EnderDragonFinishListener(challengeController));
        register.accept(new StopOnDeathListener(challengeController));
        register.accept(new BlockRandomizeListener(challengeController));
        register.accept(new MobDropRandomizeListener(challengeController));
        register.accept(new LootRandomizeListener(challengeController));

        // Others
        register.accept(new PlayerJoinListener());
        register.accept(new PlayerQuitListener(challengeController));
        register.accept(new ResourcePackStatusListener());
        register.accept(new ServerListPingListener());
        register.accept(new ServerTickListener(challengeController));
        register.accept(new ChatListener());
        register.accept(new AnvilRenameListener());
    }

}
