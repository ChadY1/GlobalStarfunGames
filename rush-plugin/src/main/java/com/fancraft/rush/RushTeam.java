package com.fancraft.rush;

import com.fancraft.core.game.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Rush team storing bed status.
 */
public class RushTeam extends Team {

    private Location bedLocation;
    private boolean bedIntact = true;

    public RushTeam(String name, ChatColor color) {
        super(name, color);
    }

    public Location getBedLocation() {
        return bedLocation;
    }

    public void setBedLocation(Location bedLocation) {
        this.bedLocation = bedLocation;
    }

    public boolean isBedIntact() {
        return bedIntact;
    }

    public void destroyBed() {
        this.bedIntact = false;
    }
}
