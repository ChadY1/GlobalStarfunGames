package com.fancraft.api;

import com.fancraft.core.npc.NpcManager;
import com.fancraft.core.proxy.ProxyBridge;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StarfunApiPlugin extends JavaPlugin {

    private NpcManager npcManager;
    private ProxyBridge proxyBridge;
    private boolean bungeeEnabled;
    private Map<String, String> routes;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.proxyBridge = new ProxyBridge(this);
        this.bungeeEnabled = getConfig().getBoolean("bungeecord.enabled", false);
        this.routes = loadRoutes();
        this.npcManager = new NpcManager(this);
        StarfunCommand command = new StarfunCommand(this, npcManager, proxyBridge, bungeeEnabled, routes);
        getCommand("starfun").setExecutor(command);
        getCommand("starfun").setTabCompleter(command);
        Bukkit.getServicesManager().register(NpcManager.class, npcManager, this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        if (npcManager != null) {
            npcManager.shutdown();
        }
    }

    private Map<String, String> loadRoutes() {
        if (getConfig().getConfigurationSection("bungeecord.routes") == null) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        for (String key : getConfig().getConfigurationSection("bungeecord.routes").getKeys(false)) {
            map.put(key.toLowerCase(), getConfig().getString("bungeecord.routes." + key, ""));
        }
        return map;
    }
}
