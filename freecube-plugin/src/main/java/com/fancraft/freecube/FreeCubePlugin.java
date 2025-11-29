package com.fancraft.freecube;

import com.fancraft.core.config.ConfigHelper;
import com.fancraft.core.version.VersionAdapter;
import com.fancraft.core.version.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for FreeCube plugin.
 */
public class FreeCubePlugin extends JavaPlugin {

    private FreeCubeWorldManager worldManager;

    @Override
    public void onEnable() {
        VersionAdapter adapter = VersionUtils.getAdapter();
        saveDefaultConfig();
        String worldName = getConfig().getString("world", "freecube_world");
        boolean protect = getConfig().getBoolean("protect", true);
        this.worldManager = new FreeCubeWorldManager(worldName,
                ConfigHelper.readLocation(ConfigHelper.requireSection(this, "hub")));
        FreeCubeListener listener = new FreeCubeListener(worldManager, protect);
        Bukkit.getPluginManager().registerEvents(listener, this);
        getCommand("freecube").setExecutor(new FreeCubeCommand(worldManager));
        getLogger().info("FreeCube actif avec l'adaptateur " + adapter.getVersionTag());
    }
}
