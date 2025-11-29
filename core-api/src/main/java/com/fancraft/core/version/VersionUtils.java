package com.fancraft.core.version;

import org.bukkit.Bukkit;

/**
 * Utility to detect server version and expose the right adapter.
 */
public final class VersionUtils {

    private static VersionAdapter adapter;

    private VersionUtils() {
    }

    public static VersionAdapter getAdapter() {
        if (adapter == null) {
            adapter = detect();
        }
        return adapter;
    }

    private static VersionAdapter detect() {
        String version = Bukkit.getBukkitVersion();
        if (version.startsWith("1.8") || version.startsWith("1.9") || version.startsWith("1.10") || version.startsWith("1.11") || version.startsWith("1.12")) {
            return new LegacyVersionAdapter();
        }
        return new ModernVersionAdapter();
    }
}
