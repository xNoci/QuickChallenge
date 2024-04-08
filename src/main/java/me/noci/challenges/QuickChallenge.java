package me.noci.challenges;

import io.papermc.lib.PaperLib;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.command.CommandTimer;
import me.noci.challenges.listeners.*;
import me.noci.challenges.worlds.WorldController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class QuickChallenge extends JavaPlugin {

    private WorldController worldController;
    private ChallengeController challengeController;

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        this.worldController = new WorldController();
        this.challengeController = new ChallengeController(worldController);
        this.challengeController.loadChallenges();

        registerListener();
        registerCommands();
    }

    @Override
    public void onDisable() {
        Component kickComponent = Component.text("Der Server wird geschlossen.", NamedTextColor.RED);
        Bukkit.getOnlinePlayers().forEach(player -> player.kick(kickComponent));
        this.challengeController.save();
        this.worldController.deleteWorlds();
    }

    private void registerListener() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(challengeController), this);
        pluginManager.registerEvents(new ResourcePackStatusListener(), this);
        pluginManager.registerEvents(new PlayerPortalListener(worldController), this);
        pluginManager.registerEvents(new EnityTargetListener(challengeController), this);
        pluginManager.registerEvents(new EntityDamageListener(challengeController), this);
        pluginManager.registerEvents(new EntityPathfindListener(challengeController), this);
        pluginManager.registerEvents(new FoodLevelChangeListener(challengeController), this);
        pluginManager.registerEvents(new PlayerMoveListener(challengeController), this);
        pluginManager.registerEvents(new BlockListener(challengeController), this);
        pluginManager.registerEvents(new ItemDropListener(challengeController), this);
        pluginManager.registerEvents(new PlayerTeleportListener(challengeController), this);
    }

    private void registerCommands() {
        new CommandTimer(this, challengeController);
    }

}
