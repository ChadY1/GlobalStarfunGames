package com.fancraft.rush;

import com.fancraft.core.game.BaseGameManager;
import com.fancraft.core.game.GameState;
import com.fancraft.core.game.GameStateChangeEvent;
import com.fancraft.core.version.VersionAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Handles Rush round lifecycle.
 */
public class RushGameManager extends BaseGameManager<RushArena> {

    public RushGameManager(Logger logger, VersionAdapter adapter) {
        super(logger, adapter);
    }

    @Override
    public void start(RushArena arena) {
        GameState previous = arena.getState();
        arena.setState(GameState.RUNNING);
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(arena.getId(), previous, arena.getState()));
        broadcast(arena, "La partie Rush commence !");
        arena.getTeams().forEach(team -> team.getMembers().forEach(p -> p.teleport(team.getSpawn())));
    }

    @Override
    public void end(RushArena arena, String reason) {
        GameState previous = arena.getState();
        arena.setState(GameState.ENDING);
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(arena.getId(), previous, arena.getState()));
        broadcast(arena, "Fin de partie : " + reason);
        arena.reset();
    }

    public Optional<RushArena> findArenaByPlayer(Player player) {
        return getArenas().stream().filter(arena -> arena.contains(player)).findFirst();
    }
}
