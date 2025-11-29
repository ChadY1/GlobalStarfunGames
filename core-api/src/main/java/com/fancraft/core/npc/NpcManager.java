package com.fancraft.core.npc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionnaire simple de PNJ combattants pilotés côté serveur.
 */
public class NpcManager {

    private final Map<UUID, ScriptedNpc> npcs = new ConcurrentHashMap<>();
    private final Map<UUID, LivingEntity> entities = new ConcurrentHashMap<>();
    private final Plugin plugin;
    private BukkitTask ticker;

    public NpcManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public ScriptedNpc spawnFighter(Location location, String arenaId, ItemStack weapon, ChatColor color) {
        World world = location.getWorld();
        LivingEntity entity = (LivingEntity) world.spawnEntity(location, EntityType.ZOMBIE);
        entity.setCustomName(color + "StarfunBot");
        entity.setCustomNameVisible(true);
        entity.getEquipment().setItemInMainHand(weapon);
        entity.setRemoveWhenFarAway(false);
        ScriptedNpc npc = new ScriptedNpc(entity.getUniqueId(), arenaId);
        npcs.put(entity.getUniqueId(), npc);
        entities.put(entity.getUniqueId(), entity);
        ensureTicker();
        return npc;
    }

    public void setPatrol(UUID npcId, Location location) {
        ScriptedNpc npc = npcs.get(npcId);
        if (npc != null) {
            npc.setPatrolPoint(location);
        }
    }

    public void assignTarget(UUID npcId, org.bukkit.entity.Player target) {
        ScriptedNpc npc = npcs.get(npcId);
        if (npc != null) {
            npc.setTarget(target);
        }
    }

    public void clearArenaBots(String arenaId) {
        npcs.entrySet().removeIf(entry -> {
            ScriptedNpc npc = entry.getValue();
            if (npc.getArenaId().equalsIgnoreCase(arenaId)) {
                Entity entity = entities.get(entry.getKey());
                if (entity != null) {
                    entity.remove();
                }
                entities.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }

    public void shutdown() {
        npcs.keySet().forEach(id -> {
            Entity entity = entities.get(id);
            if (entity != null) {
                entity.remove();
            }
        });
        npcs.clear();
        entities.clear();
        if (ticker != null) {
            ticker.cancel();
            ticker = null;
        }
    }

    private void ensureTicker() {
        if (ticker != null) {
            return;
        }
        ticker = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Iterator<Map.Entry<UUID, ScriptedNpc>> iterator = npcs.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, ScriptedNpc> entry = iterator.next();
                Entity entity = entities.get(entry.getKey());
                if (!(entity instanceof LivingEntity)) {
                    iterator.remove();
                    entities.remove(entry.getKey());
                    continue;
                }
                entry.getValue().tick((LivingEntity) entity);
            }
        }, 20L, 20L);
    }
}
