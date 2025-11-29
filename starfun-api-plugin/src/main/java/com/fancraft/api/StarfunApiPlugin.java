package com.fancraft.api;

import com.fancraft.core.npc.NpcManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class StarfunApiPlugin extends JavaPlugin {

    private NpcManager npcManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.npcManager = new NpcManager(this);
        StarfunCommand command = new StarfunCommand(this, npcManager);
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
}
