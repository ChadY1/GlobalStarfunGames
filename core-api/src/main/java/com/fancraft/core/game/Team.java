package com.fancraft.core.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Basic team representation shared across mini-games.
 */
public class Team {

    private final String name;
    private final ChatColor color;
    private final Set<Player> members = new HashSet<>();
    private Location spawn;

    public Team(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void addPlayer(Player player) {
        members.add(player);
    }

    public void removePlayer(Player player) {
        members.remove(player);
    }

    public boolean isMember(Player player) {
        return members.contains(player);
    }

    public Set<Player> getMembers() {
        return Collections.unmodifiableSet(members);
    }
}
