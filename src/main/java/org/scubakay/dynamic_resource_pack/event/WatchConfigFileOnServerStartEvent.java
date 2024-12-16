package org.scubakay.dynamic_resource_pack.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.scubakay.dynamic_resource_pack.util.ConfigFileWatcher;

public class WatchConfigFileOnServerStartEvent implements ServerLifecycleEvents.ServerStarted {
    @Override
    public void onServerStarted(MinecraftServer server) {
        if (ConfigFileWatcher.instance == null) {
            ConfigFileWatcher.instance = new ConfigFileWatcher(server);
            ConfigFileWatcher.instance.setDaemon(true);
            ConfigFileWatcher.instance.start();
        }
    }
}
