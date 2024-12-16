package org.scubakay.dynamic_resource_pack.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.scubakay.dynamic_resource_pack.util.ConfigFileWatcher;

public class CloseConfigFileWatcherEvent implements ServerLifecycleEvents.ServerStopping {
    @Override
    public void onServerStopping(MinecraftServer server) {
        if (ConfigFileWatcher.instance != null) {
            ConfigFileWatcher.instance.stopThread();
        }
    }
}
