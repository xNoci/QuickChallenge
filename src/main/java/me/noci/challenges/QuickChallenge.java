package me.noci.challenges;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.command.CommandAllItems;
import me.noci.challenges.command.CommandChallenge;
import me.noci.challenges.command.CommandTimer;
import me.noci.challenges.listeners.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class QuickChallenge extends JavaPlugin {

    @Getter private static QuickChallenge instance;

    private Config config;
    private ChallengeController challengeController;

    @Override
    public void onEnable() {
        if (!PaperLib.isPaper()) {
            Bukkit.getPluginManager().disablePlugin(this);
            getLogger().info("This plugin only works with PaperMC.");
            return;
        }

        instance = this;

        this.config = new Config(this, "config.yml", false, true);

        this.challengeController = new ChallengeController();
        this.challengeController.tryLoadChallenge();

        registerListener();
        registerCommands();
    }

    @Override
    public void onDisable() {
        Component kickComponent = Component.text("Der Server wird geschlossen.", NamedTextColor.RED);
        this.challengeController.stopChallenges();
        this.challengeController.save();
        Bukkit.getOnlinePlayers().forEach(player -> player.kick(kickComponent));
    }

    private void registerListener() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(challengeController), this);
        pluginManager.registerEvents(new ResourcePackStatusListener(), this);
        pluginManager.registerEvents(new EnityTargetListener(challengeController), this);
        pluginManager.registerEvents(new EntityDamageListener(challengeController), this);
        pluginManager.registerEvents(new EntityMoveListener(challengeController), this);
        pluginManager.registerEvents(new FoodLevelChangeListener(challengeController), this);
        pluginManager.registerEvents(new PlayerMoveListener(challengeController), this);
        pluginManager.registerEvents(new BlockListener(challengeController), this);
        pluginManager.registerEvents(new ItemDropListener(challengeController), this);
        pluginManager.registerEvents(new ServerListPingListener(config), this);
    }

    private void registerCommands() {
        new CommandChallenge(this, challengeController);
        new CommandTimer(this, challengeController);
        new CommandAllItems(this, challengeController);
    }

}
