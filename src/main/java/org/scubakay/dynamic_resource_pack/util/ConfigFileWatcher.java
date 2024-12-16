package org.scubakay.dynamic_resource_pack.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.scubakay.dynamic_resource_pack.DynamicResourcePack;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigFileWatcher extends Thread {
    public static ConfigFileWatcher instance;

    private AtomicBoolean stop = new AtomicBoolean(false);
    private MinecraftServer server;

    public ConfigFileWatcher(MinecraftServer server) {
        this.server = server;
    }

    public boolean isStopped() { return stop.get(); }
    public void stopThread() { stop.set(true); }

    public void doOnChange() {
        // Do whatever action you want here
        DynamicResourcePack.LOGGER.info(DynamicResourcePack.getModConfigFile().getFileName() + " has changed, reloading resource pack...");
        DynamicResourcePack.loadConfigFile();
        ResourcePackHandler.pushTo(server);
    }

    @Override
    public void run() {
        final Path path = DynamicResourcePack.getModConfigFolder();
        DynamicResourcePack.LOGGER.info("Watching " + DynamicResourcePack.getModConfigFile().getFileName() + " for changes");
        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                Thread.sleep( 50 );
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    //we only register "ENTRY_MODIFY" so the context is always a Path.
                    final Path changed = (Path) event.context();
                    if (changed.endsWith(DynamicResourcePack.getModConfigFile().getFileName())) {
                        doOnChange();
                    }
                }
                // reset the key
                boolean valid = wk.reset();
                if (!valid) {
                    DynamicResourcePack.LOGGER.error("Key has been unregistered");
                }
            }
        } catch (Exception ex) {
            DynamicResourcePack.LOGGER.error(ex.toString());
        }
    }
}