package com.fancraft.freecube;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Basic protection listener for the creative world.
 */
public class FreeCubeListener implements Listener {

    private final FreeCubeWorldManager worldManager;
    private final boolean protect;

    public FreeCubeListener(FreeCubeWorldManager worldManager, boolean protect) {
        this.worldManager = worldManager;
        this.protect = protect;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        worldManager.sendToCreative(event.getPlayer());
        event.getPlayer().sendMessage(ChatColor.AQUA + "Bienvenue dans FreeCube.");
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getTo() != null && event.getTo().getWorld().equals(worldManager.getWorld())) {
            event.getPlayer().setAllowFlight(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!protect) {
            return;
        }
        if (!event.getPlayer().hasPermission("freecube.build")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Construction protégée.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!protect) {
            return;
        }
        if (!event.getPlayer().hasPermission("freecube.build")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Destruction protégée.");
        }
    }
}
