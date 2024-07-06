package me.noci.challenges;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.command.CommandAllItems;
import me.noci.challenges.command.CommandChallenge;
import me.noci.challenges.command.CommandTimer;
import me.noci.challenges.listeners.*;
import me.noci.challenges.settings.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class QuickChallenge extends JavaPlugin {

    @Getter private static QuickChallenge instance;

    @Getter private Config config;
    private ChallengeController challengeController;

    @Override
    public void onEnable() {
        if (!PaperLib.isPaper()) {
            Bukkit.getPluginManager().disablePlugin(this);
            getLogger().info("This plugin only works with PaperMC.");
            return;
        }

        instance = this;

        this.config = Config.create(this, "config.yml", false, true);

        this.challengeController = new ChallengeController();
        this.challengeController.tryLoadChallenge();

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        Component kickComponent = Component.text("Der Server wird geschlossen.", NamedTextColor.RED);
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
        register.accept(new ServerListPingListener(config));
        register.accept(new ServerTickListener(challengeController));
        register.accept(new ChatListener(config));
        register.accept(new AnvilRenameListener(config));
        register.accept(new PlayerInteractListener(challengeController));
    }

    private void registerCommands() {
        new CommandChallenge(this, challengeController);
        new CommandTimer(this, challengeController);
        new CommandAllItems(this, challengeController, config);
    }

}
