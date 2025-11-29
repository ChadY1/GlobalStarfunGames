package com.fancraft.rush;

import com.fancraft.core.game.GameState;
import com.fancraft.core.version.VersionAdapter;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Listener hooking Rush gameplay events.
 */
public class RushListener implements Listener {

    private final RushGameManager gameManager;
    private final VersionAdapter versionAdapter;

    public RushListener(RushGameManager gameManager, VersionAdapter versionAdapter) {
        this.gameManager = gameManager;
        this.versionAdapter = versionAdapter;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (gameManager.getArenas().isEmpty()) {
            RushArena arena = new RushArena("default");
            gameManager.registerArena(arena);
        }
        RushArena arena = gameManager.getArenas().get(0);
        gameManager.joinPlayer(arena, event.getPlayer());
        event.getPlayer().sendMessage(ChatColor.GREEN + "Rejoint l'arène Rush.");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        gameManager.findArenaByPlayer(player).ifPresent(arena -> arena.getTeams().forEach(team -> {
            if (team.getBedLocation() != null && team.getBedLocation().getBlock().equals(block)) {
                team.destroyBed();
                gameManager.broadcast(arena, team.getColor() + team.getName() + ChatColor.WHITE + " a perdu son lit !");
            }
        }));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        gameManager.findArenaByPlayer(event.getPlayer()).ifPresent(arena -> arena.getTeams().stream()
                .filter(team -> team.isMember(event.getPlayer()))
                .findFirst()
                .ifPresent(team -> event.setRespawnLocation(team.getSpawn())));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        gameManager.findArenaByPlayer(event.getEntity()).ifPresent(arena -> {
            RushTeam eliminatedTeam = arena.getTeams().stream()
                    .filter(team -> team.isMember(event.getEntity()))
                    .findFirst().orElse(null);
            if (eliminatedTeam != null && !eliminatedTeam.isBedIntact()) {
                gameManager.broadcast(arena, eliminatedTeam.getColor() + eliminatedTeam.getName() + ChatColor.WHITE + " est éliminée.");
                gameManager.leavePlayer(arena, event.getEntity());
                if (arena.getTeams().stream().filter(RushTeam::isBedIntact).count() == 1) {
                    RushTeam winner = arena.getTeams().stream().filter(RushTeam::isBedIntact).findFirst().get();
                    gameManager.end(arena, winner.getColor() + winner.getName() + ChatColor.WHITE + " gagne !");
                }
            }
        });
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        gameManager.findArenaByPlayer(damager).ifPresent(arena -> {
            if (arena.getState() != GameState.RUNNING) {
                event.setCancelled(true);
                damager.sendMessage(ChatColor.RED + "La partie n'a pas démarré.");
            }
            if (arena.contains(victim)) {
                victim.playSound(victim.getLocation(), versionAdapter.getGoalSound(), 1f, 1f);
            }
        });
    }
}
