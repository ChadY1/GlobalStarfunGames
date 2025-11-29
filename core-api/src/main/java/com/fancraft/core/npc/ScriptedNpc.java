package com.fancraft.core.npc;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Représente un PNJ piloté servant aux tests PVP.
 */
public class ScriptedNpc {

    private final UUID entityId;
    private final String arenaId;
    private Player target;
    private Location patrolPoint;

    public ScriptedNpc(UUID entityId, String arenaId) {
        this.entityId = entityId;
        this.arenaId = arenaId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public String getArenaId() {
        return arenaId;
    }

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public Location getPatrolPoint() {
        return patrolPoint;
    }

    public void setPatrolPoint(Location patrolPoint) {
        this.patrolPoint = patrolPoint;
    }

    public void tick(LivingEntity entity) {
        if (target != null && target.isOnline()) {
            if (entity instanceof org.bukkit.entity.Creature) {
                ((org.bukkit.entity.Creature) entity).setTarget(target);
            }
        } else if (patrolPoint != null) {
            Location current = entity.getLocation();
            if (current.distanceSquared(patrolPoint) > 1.5) {
                entity.setVelocity(patrolPoint.toVector().subtract(current.toVector()).normalize().multiply(0.2));
            }
        }
    }
}
