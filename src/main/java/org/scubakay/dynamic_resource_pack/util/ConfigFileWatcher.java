package org.scubakay.dynamic_resource_pack.util;

import org.scubakay.dynamic_resource_pack.DynamicResourcePack;

import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigFileWatcher extends Thread {
    private final AtomicBoolean stop = new AtomicBoolean(false);
    private final Runnable runnable;
    private final Path directory;
    private final Path file;

    public ConfigFileWatcher(Path directory, Path file, Runnable runnable) {
        this.runnable = runnable;
        this.file = file;
        this.directory = directory;
    }

    public void stopThread() { stop.set(true); }

    @Override
    public void run() {
        DynamicResourcePack.LOGGER.info("Watching " + file.getFileName() + " for changes");
        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            final WatchKey watchKey = directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                Thread.sleep( 50 );
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    //we only register "ENTRY_MODIFY" so the context is always a Path.
                    final Path changed = (Path) event.context();
                    if (changed.endsWith(file.getFileName())) {
                        runnable.run();
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