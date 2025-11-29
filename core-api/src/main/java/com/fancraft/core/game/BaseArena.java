package com.fancraft.core.game;

import com.fancraft.core.scoreboard.ScoreboardService;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Base arena scaffold to manage players and common attributes.
 */
public abstract class BaseArena {

    private final String id;
    private final Set<UUID> players = new HashSet<>();
    private GameState state = GameState.WAITING;
    private ScoreboardService scoreboardService;

    protected BaseArena(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setScoreboardService(ScoreboardService scoreboardService) {
        this.scoreboardService = scoreboardService;
    }

    public ScoreboardService getScoreboardService() {
        return scoreboardService;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public boolean contains(Player player) {
        return players.contains(player.getUniqueId());
    }

    public abstract void reset();
}
