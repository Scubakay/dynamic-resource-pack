package org.scubakay.dynamic_resource_pack.util;

import org.scubakay.dynamic_resource_pack.DynamicResourcePack;

import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigFileWatcher extends Thread {
    private final AtomicBoolean stop = new AtomicBoolean(false);
    private final Runnable runnable;
    private final Path directory;
    private final Path file;
    private static final int DEBOUNCE_MILLIS = 5000; // 5 seconds
    private volatile long lastTriggerTime = 0;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private volatile ScheduledFuture<?> scheduledTask = null;

    public ConfigFileWatcher(Path directory, Path file, Runnable runnable) {
        this.runnable = runnable;
        this.file = file;
        this.directory = directory;
    }

    public void stopThread() {
        stop.set(true);
        executor.shutdownNow();
    }

    @Override
    public void run() {
        DynamicResourcePack.LOGGER.info("Watching " + file.getFileName() + " for changes");
        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!stop.get()) {
                //noinspection BusyWait
                Thread.sleep(50);
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    final Path changed = (Path) event.context();
                    if (changed.endsWith(file.getFileName())) {
                        long now = System.currentTimeMillis();
                        if (now - lastTriggerTime > DEBOUNCE_MILLIS) {
                            lastTriggerTime = now;
                            if (scheduledTask != null && !scheduledTask.isDone()) {
                                scheduledTask.cancel(false);
                            }
                            DynamicResourcePack.LOGGER.info("{} has changed, scheduling resource pack reload...", file.getFileName());
                            scheduledTask = executor.schedule(runnable, 1, TimeUnit.SECONDS);
                        } else {
                            DynamicResourcePack.LOGGER.info("Debounced duplicate event {} for {}", event.kind().name(), file.getFileName());
                        }
                    }
                }
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
