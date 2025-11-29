package com.fancraft.core.game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Custom event fired by managers when the state of an arena changes.
 */
public class GameStateChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String arenaId;
    private final GameState previous;
    private final GameState current;

    public GameStateChangeEvent(String arenaId, GameState previous, GameState current) {
        this.arenaId = arenaId;
        this.previous = previous;
        this.current = current;
    }

    public String getArenaId() {
        return arenaId;
    }

    public GameState getPrevious() {
        return previous;
    }

    public GameState getCurrent() {
        return current;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
