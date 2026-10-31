package me.noci.challenges.paper;

import lombok.Getter;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.command.CommandAllItems;
import me.noci.challenges.command.CommandChallenge;
import me.noci.challenges.command.CommandTimer;
import me.noci.challenges.listeners.AnvilRenameListener;
import me.noci.challenges.listeners.BlockListener;
import me.noci.challenges.listeners.ChatListener;
import me.noci.challenges.listeners.EnityTargetListener;
import me.noci.challenges.listeners.EntityDamageListener;
import me.noci.challenges.listeners.EntityMoveListener;
import me.noci.challenges.listeners.FoodLevelChangeListener;
import me.noci.challenges.listeners.ItemDropListener;
import me.noci.challenges.listeners.PlayerInteractListener;
import me.noci.challenges.listeners.PlayerJoinListener;
import me.noci.challenges.listeners.PlayerMoveListener;
import me.noci.challenges.listeners.PlayerQuitListener;
import me.noci.challenges.listeners.ResourcePackStatusListener;
import me.noci.challenges.listeners.ServerListPingListener;
import me.noci.challenges.listeners.ServerTickListener;
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
        this.challengeController.stopChallenges();
        this.challengeController.save();
        Bukkit.getOnlinePlayers().forEach(player -> player.kick(kickComponent));
    }

    private void registerListeners() {
        Consumer<Listener> register = listener -> getServer().getPluginManager().registerEvents(listener, this);

        register.accept(new PlayerJoinListener());
        register.accept(new PlayerQuitListener(challengeController));
        register.accept(new ResourcePackStatusListener());
        register.accept(new EnityTargetListener(challengeController));
        register.accept(new EntityDamageListener(challengeController));
        register.accept(new EntityMoveListener(challengeController));
        register.accept(new FoodLevelChangeListener(challengeController));
        register.accept(new PlayerMoveListener(challengeController));
        register.accept(new BlockListener(challengeController));
        register.accept(new ItemDropListener(challengeController));
        register.accept(new ServerListPingListener());
        register.accept(new ServerTickListener(challengeController));
        register.accept(new ChatListener());
        register.accept(new AnvilRenameListener());
        register.accept(new PlayerInteractListener(challengeController));
    }

}
