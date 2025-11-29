package com.fancraft.core;

import com.fancraft.core.api.StarfunService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Minimal plugin entry point so the shared core can be dropped into a plugins folder
 * without missing-description errors. StarfunService is registered when present,
 * but the jar primarily serves as a shaded dependency for game plugins.
 */
public class CoreApiPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Starfun Core API initialisée (utilisation principale en dépendance shade).");

        // Expose a lightweight StarfunService for introspection and help listing.
        Bukkit.getServicesManager().register(StarfunService.class, new StarfunService() {
            @Override
            public String getGameKey() {
                return "core";
            }

            @Override
            public String getDisplayName() {
                return "Starfun Core API";
            }

            @Override
            public void sendHelp(CommandSender sender) {
                sender.sendMessage("§7Core API partagé — aucune commande dédiée, utilisez les plugins mini-jeux.");
            }

            @Override
            public void reloadConfig(CommandSender sender) {
                sender.sendMessage("§7Aucun rechargement nécessaire pour core-api (stateless).");
            }

            @Override
            public boolean handleAdminCommand(CommandSender sender, String[] args) {
                // Nothing to handle; return false to let caller know no action was taken.
                return false;
            }
        }, this, ServicePriority.Lowest);
    }
}
