package com.fancraft.hikabrain;

import com.fancraft.core.scoreboard.ScoreboardService;
import com.fancraft.core.version.VersionAdapter;
import com.fancraft.core.version.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for HikaBrain plugin.
 */
public class HikaBrainPlugin extends JavaPlugin {

    private HikaGameManager gameManager;

    @Override
    public void onEnable() {
        VersionAdapter adapter = VersionUtils.getAdapter();
        ScoreboardService scoreboardService = new ScoreboardService();
        this.gameManager = new HikaGameManager(getLogger(), adapter, 5);
        this.gameManager.setScoreboardService(scoreboardService);
        HikaListener listener = new HikaListener(gameManager, adapter);
        Bukkit.getPluginManager().registerEvents(listener, this);
        getCommand("hikabrain").setExecutor(new HikaCommand(gameManager));
        getLogger().info("HikaBrain prêt avec l'adaptateur " + adapter.getVersionTag());
    }

    @Override
    public void onDisable() {
        gameManager.getArenas().forEach(HikaArena::reset);
    }
}
