package com.fancraft.core.game;

import com.fancraft.core.scoreboard.ScoreboardService;
import com.fancraft.core.version.VersionAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Lightweight base manager controlling lifecycle and messaging for arenas.
 */
public abstract class BaseGameManager<A extends BaseArena> {

    private final Logger logger;
    private final List<A> arenas = new ArrayList<>();
    private final VersionAdapter versionAdapter;
    private ScoreboardService scoreboardService;

    protected BaseGameManager(Logger logger, VersionAdapter versionAdapter) {
        this.logger = logger;
        this.versionAdapter = versionAdapter;
    }

    public void setScoreboardService(ScoreboardService scoreboardService) {
        this.scoreboardService = scoreboardService;
    }

    public VersionAdapter getVersionAdapter() {
        return versionAdapter;
    }

    public void registerArena(A arena) {
        arenas.add(arena);
        if (scoreboardService != null) {
            arena.setScoreboardService(scoreboardService);
        }
    }

    public void clearArenas() {
        arenas.clear();
    }

    public Optional<A> getArena(String id) {
        return arenas.stream().filter(a -> a.getId().equalsIgnoreCase(id)).findFirst();
    }

    public List<A> getArenas() {
        return arenas;
    }

    public void broadcast(A arena, String message) {
        arena.getPlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(java.util.Objects::nonNull)
                .forEach(player -> player.sendMessage(message));
    }

    public void joinPlayer(A arena, Player player) {
        arena.addPlayer(player);
        if (arena.getScoreboardService() != null) {
            arena.getScoreboardService().apply(player, arena.getId());
        }
    }

    public void leavePlayer(A arena, Player player) {
        arena.removePlayer(player);
        if (arena.getScoreboardService() != null) {
            arena.getScoreboardService().clear(player);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public abstract void start(A arena);

    public abstract void end(A arena, String reason);
}
