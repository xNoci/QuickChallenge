package me.noci.challenges.paper;

import lombok.Getter;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.listeners.*;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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
