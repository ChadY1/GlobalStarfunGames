package com.fancraft.skywars;

import com.fancraft.core.config.ConfigHelper;
import com.fancraft.core.scoreboard.ScoreboardService;
import com.fancraft.core.version.VersionAdapter;
import com.fancraft.core.version.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
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
        saveDefaultConfig();
        try {
            loadArena();
        } catch (IllegalArgumentException ex) {
            getLogger().severe("Configuration SkyWars invalide: " + ex.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        SkyWarsListener listener = new SkyWarsListener(manager, adapter);
        Bukkit.getPluginManager().registerEvents(listener, this);
        getCommand("skywars").setExecutor(new SkyWarsCommand(manager));
        getLogger().info("SkyWars activé avec l'adaptateur " + adapter.getVersionTag());
    }

    @Override
    public void onDisable() {
        manager.getArenas().forEach(SkyWarsArena::reset);
    }

    private void loadArena() {
        String arenaId = ConfigHelper.requireSection(this, "arenas").getKeys(false).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucune arène SkyWars définie"));
        SkyWarsArena arena = new SkyWarsArena(arenaId);
        arena.setChestManager(new SkyWarsChestManager());
        arena.setLobbySpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, "arenas." + arenaId + ".lobby")));
        ConfigurationSection spawns = ConfigHelper.requireSection(this, "arenas." + arenaId + ".spawns");
        spawns.getKeys(false).forEach(key -> arena.getSpawnPoints()
                .add(ConfigHelper.readLocation(spawns.getConfigurationSection(key))));
        ConfigurationSection chests = ConfigHelper.requireSection(this, "arenas." + arenaId + ".chests");
        ConfigurationSection islands = ConfigHelper.requireSection(this, "arenas." + arenaId + ".chests.islands");
        islands.getKeys(false).forEach(key -> arena.getIslandChests()
                .add(ConfigHelper.readLocation(islands.getConfigurationSection(key))));
        ConfigurationSection center = ConfigHelper.requireSection(this, "arenas." + arenaId + ".chests.center");
        center.getKeys(false).forEach(key -> arena.getCenterChests()
                .add(ConfigHelper.readLocation(center.getConfigurationSection(key))));
        manager.registerArena(arena);
        getLogger().info(ChatColor.GREEN + "Arène SkyWars chargée: " + arena.getId());
    }
}
