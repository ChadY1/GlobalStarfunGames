package com.fancraft.hikabrain;

import com.fancraft.core.game.BaseArena;
import com.fancraft.core.game.GameState;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

/**
 * Arena definition for MLGRush/HikaBrain.
 */
public class HikaArena extends BaseArena {

    private final List<HikaTeam> teams;

    public HikaArena(String id) {
        super(id);
        teams = Arrays.asList(new HikaTeam("Rouge", ChatColor.RED), new HikaTeam("Bleu", ChatColor.BLUE));
    }

    public List<HikaTeam> getTeams() {
        return teams;
    }

    @Override
    public void reset() {
        setState(GameState.RESETTING);
        teams.forEach(team -> {
            team.resetScore();
            team.getMembers().forEach(player -> player.teleport(player.getWorld().getSpawnLocation()));
        });
        getPlayers().clear();
        setState(GameState.WAITING);
    }
}
