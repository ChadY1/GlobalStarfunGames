package com.fancraft.rush;

import com.fancraft.core.game.BaseArena;
import com.fancraft.core.game.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds Rush-specific data such as beds and generators.
 */
public class RushArena extends BaseArena {

    private final List<RushTeam> teams = new ArrayList<>(2);

    public RushArena(String id) {
        super(id);
        teams.add(new RushTeam("Rouge", org.bukkit.ChatColor.RED));
        teams.add(new RushTeam("Bleu", org.bukkit.ChatColor.BLUE));
    }

    public List<RushTeam> getTeams() {
        return teams;
    }

    public void reset() {
        setState(GameState.RESETTING);
        teams.forEach(team -> {
            team.destroyBed();
            team.setBedLocation(null);
            team.getMembers().forEach(player -> player.teleport(player.getWorld().getSpawnLocation()));
        });
        getPlayers().clear();
        setState(GameState.WAITING);
    }
}
