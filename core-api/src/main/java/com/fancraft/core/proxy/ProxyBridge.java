package com.fancraft.core.proxy;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Lightweight helper around the BungeeCord plugin messaging channel.
 */
public class ProxyBridge {

    private final JavaPlugin plugin;

    public ProxyBridge(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    /**
     * Request a transfer for the given player to the named BungeeCord server.
     *
     * @param player    player to move
     * @param serverKey target server name defined in proxy config
     */
    public void connect(Player player, String serverKey) {
        if (serverKey == null || serverKey.trim().isEmpty()) {
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverKey.trim());
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
