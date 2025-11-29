package com.fancraft.hikabrain;

import com.fancraft.core.game.BaseGameManager;
import com.fancraft.core.game.GameState;
import com.fancraft.core.game.GameStateChangeEvent;
import com.fancraft.core.version.VersionAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Lifecycle handler for HikaBrain matches.
 */
public class HikaGameManager extends BaseGameManager<HikaArena> {

    private final int maxScore;

    public HikaGameManager(Logger logger, VersionAdapter adapter, int maxScore) {
        super(logger, adapter);
        this.maxScore = maxScore;
    }

    @Override
    public void start(HikaArena arena) {
        GameState previous = arena.getState();
        arena.setState(GameState.RUNNING);
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(arena.getId(), previous, arena.getState()));
        broadcast(arena, "Début du duel HikaBrain !");
        arena.getTeams().forEach(team -> team.getMembers().forEach(player -> player.teleport(team.getSpawn())));
    }

    @Override
    public void end(HikaArena arena, String reason) {
        GameState previous = arena.getState();
        arena.setState(GameState.ENDING);
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(arena.getId(), previous, arena.getState()));
        broadcast(arena, "Fin du duel : " + reason);
        arena.reset();
    }

    public void score(HikaArena arena, HikaTeam scoringTeam) {
        scoringTeam.addPoint();
        broadcast(arena, scoringTeam.getColor() + scoringTeam.getName() + " marque (" + scoringTeam.getScore() + "/" + maxScore + ")");
        if (scoringTeam.getScore() >= maxScore) {
            end(arena, scoringTeam.getColor() + scoringTeam.getName() + " gagne");
        } else {
            arena.setState(GameState.STARTING);
            arena.getTeams().forEach(team -> team.getMembers().forEach(player -> player.teleport(team.getSpawn())));
            arena.setState(GameState.RUNNING);
        }
    }

    public Optional<HikaArena> findArenaByPlayer(Player player) {
        return getArenas().stream().filter(arena -> arena.contains(player)).findFirst();
    }
}
