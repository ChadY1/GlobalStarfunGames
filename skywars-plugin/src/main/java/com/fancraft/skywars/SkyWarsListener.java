package com.fancraft.skywars;

import com.fancraft.core.game.GameState;
import com.fancraft.core.version.VersionAdapter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Event listener for SkyWars mechanics.
 */
public class SkyWarsListener implements Listener {

    private final SkyWarsGameManager manager;
    private final VersionAdapter adapter;

    public SkyWarsListener(SkyWarsGameManager manager, VersionAdapter adapter) {
        this.manager = manager;
        this.adapter = adapter;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (manager.getArenas().isEmpty()) {
            SkyWarsArena arena = new SkyWarsArena("default");
            arena.setChestManager(new SkyWarsChestManager());
            manager.registerArena(arena);
        }
        SkyWarsArena arena = manager.getArenas().get(0);
        manager.joinPlayer(arena, event.getPlayer());
        event.getPlayer().sendMessage(ChatColor.GREEN + "Rejoint SkyWars.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() == Material.CHEST) {
            Player player = event.getPlayer();
            manager.findArenaByPlayer(player).ifPresent(arena -> {
                Chest chest = (Chest) event.getClickedBlock().getState();
                boolean isCenter = arena.getChestManager() != null && arena.getChestManager().equals(arena.getChestManager());
                arena.getChestManager().fill(chest.getInventory(), isCenter);
            });
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        manager.findArenaByPlayer(event.getEntity()).ifPresent(arena -> {
            manager.leavePlayer(arena, event.getEntity());
            manager.broadcast(arena, ChatColor.RED + event.getEntity().getName() + " est éliminé.");
            if (arena.getPlayers().size() == 1) {
                Player winner = event.getEntity().getKiller();
                String winnerName = winner != null ? winner.getName() : "Survivant";
                manager.end(arena, winnerName + " gagne");
            }
        });
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        manager.findArenaByPlayer(event.getPlayer()).ifPresent(arena -> {
            event.getPlayer().sendMessage(ChatColor.GRAY + "Mode spectateur actif.");
            event.getPlayer().setAllowFlight(true);
            event.getPlayer().setFlying(true);
            event.setRespawnLocation(event.getPlayer().getWorld().getSpawnLocation());
            if (arena.getState() != GameState.RUNNING) {
                event.getPlayer().setAllowFlight(false);
            }
        });
    }
}
