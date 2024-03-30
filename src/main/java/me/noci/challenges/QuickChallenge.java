package me.noci.challenges;

import io.papermc.lib.PaperLib;
import me.noci.challenges.listeners.PlayerJoinListener;
import me.noci.challenges.listeners.ResourcePackStatusListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class QuickChallenge extends JavaPlugin {

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        registerListener();
    }

    @Override
    public void onDisable() {

    }

    private void registerListener() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new ResourcePackStatusListener(), this);
    }

}
