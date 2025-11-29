package com.fancraft.skywars;

import com.fancraft.core.game.BaseGameManager;
import com.fancraft.core.game.GameState;
import com.fancraft.core.game.GameStateChangeEvent;
import com.fancraft.core.version.VersionAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Controls SkyWars matches: start/end and loot setup.
 */
public class SkyWarsGameManager extends BaseGameManager<SkyWarsArena> {

    public SkyWarsGameManager(Logger logger, VersionAdapter adapter) {
        super(logger, adapter);
    }

    @Override
    public void start(SkyWarsArena arena) {
        GameState previous = arena.getState();
        arena.setState(GameState.RUNNING);
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(arena.getId(), previous, arena.getState()));
        broadcast(arena, "SkyWars lancé !");
        if (arena.getChestManager() != null) {
            arena.getChestManager();
        }
        arena.getSpawnPoints().forEach(location -> {
            Player player = Bukkit.getPlayerExact(location.getWorld().getPlayers().stream().findFirst().map(Player::getName).orElse(""));
            if (player != null) {
                player.teleport(location);
            }
        });
    }

    @Override
    public void end(SkyWarsArena arena, String reason) {
        GameState previous = arena.getState();
        arena.setState(GameState.ENDING);
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(arena.getId(), previous, arena.getState()));
        broadcast(arena, "Fin de SkyWars : " + reason);
        arena.reset();
    }

    public Optional<SkyWarsArena> findArenaByPlayer(Player player) {
        return getArenas().stream().filter(arena -> arena.contains(player)).findFirst();
    }
}
