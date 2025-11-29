package com.fancraft.hikabrain;

import com.fancraft.core.game.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Team representation for HikaBrain.
 */
public class HikaTeam extends Team {

    private Location bedLocation;
    private int score;

    public HikaTeam(String name, ChatColor color) {
        super(name, color);
    }

    public Location getBedLocation() {
        return bedLocation;
    }

    public void setBedLocation(Location bedLocation) {
        this.bedLocation = bedLocation;
    }

    public int getScore() {
        return score;
    }

    public void addPoint() {
        this.score++;
    }

    public void resetScore() {
        this.score = 0;
    }
}
