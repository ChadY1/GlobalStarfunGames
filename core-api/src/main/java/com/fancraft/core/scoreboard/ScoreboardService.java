package com.fancraft.core.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple scoreboard wrapper shared by plugins.
 */
public class ScoreboardService {

    private final Map<Player, Scoreboard> scoreboards = new HashMap<>();

    public void apply(Player player, String title) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("info", "dummy");
        objective.setDisplayName(ChatColor.AQUA + title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore(ChatColor.YELLOW + "Etat: attente").setScore(1);
        player.setScoreboard(scoreboard);
        scoreboards.put(player, scoreboard);
    }

    public void update(Player player, String line, int score) {
        Scoreboard scoreboard = scoreboards.get(player);
        if (scoreboard == null) {
            return;
        }
        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective != null) {
            objective.getScore(line).setScore(score);
        }
    }

    public void clear(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        scoreboards.remove(player);
    }
}
