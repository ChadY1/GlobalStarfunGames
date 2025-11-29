package com.fancraft.hikabrain;

import com.fancraft.core.version.VersionAdapter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Listener to score and reset MLGRush rounds.
 */
public class HikaListener implements Listener {

    private final HikaGameManager gameManager;
    private final VersionAdapter adapter;

    public HikaListener(HikaGameManager gameManager, VersionAdapter adapter) {
        this.gameManager = gameManager;
        this.adapter = adapter;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        gameManager.findArenaByPlayer(player).ifPresent(arena -> arena.getTeams().forEach(team -> {
            if (team.isMember(player) && event.getTo() != null && team.getBedLocation() != null &&
                    event.getTo().distanceSquared(team.getBedLocation()) < 1.2) {
                gameManager.score(arena, team);
                player.playSound(player.getLocation(), adapter.getGoalSound(), 1f, 1f);
            }
        }));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        gameManager.findArenaByPlayer(event.getPlayer()).ifPresent(arena -> event.setCancelled(true));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        gameManager.findArenaByPlayer(event.getEntity()).ifPresent(arena -> event.getEntity().sendMessage(ChatColor.GRAY + "Vous réapparaissez pour le prochain point."));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        gameManager.findArenaByPlayer(event.getPlayer()).ifPresent(arena -> arena.getTeams().stream()
                .filter(team -> team.isMember(event.getPlayer()))
                .findFirst().ifPresent(team -> event.setRespawnLocation(team.getSpawn())));
    }
}
