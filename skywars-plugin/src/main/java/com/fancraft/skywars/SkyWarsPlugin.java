package com.fancraft.skywars;

import com.fancraft.core.scoreboard.ScoreboardService;
import com.fancraft.core.version.VersionAdapter;
import com.fancraft.core.version.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point for the SkyWars plugin.
 */
public class SkyWarsPlugin extends JavaPlugin {

    private SkyWarsGameManager manager;

    @Override
    public void onEnable() {
        VersionAdapter adapter = VersionUtils.getAdapter();
        ScoreboardService scoreboardService = new ScoreboardService();
        this.manager = new SkyWarsGameManager(getLogger(), adapter);
        this.manager.setScoreboardService(scoreboardService);
        SkyWarsListener listener = new SkyWarsListener(manager, adapter);
        Bukkit.getPluginManager().registerEvents(listener, this);
        getCommand("skywars").setExecutor(new SkyWarsCommand(manager));
        getLogger().info("SkyWars activé avec l'adaptateur " + adapter.getVersionTag());
    }

    @Override
    public void onDisable() {
        manager.getArenas().forEach(SkyWarsArena::reset);
    }
}
