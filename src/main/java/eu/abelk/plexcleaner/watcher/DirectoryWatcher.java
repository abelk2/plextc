package eu.abelk.plexcleaner.watcher;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DirectoryWatcher {
    private final WatchService watchService;
    private final Map<WatchKey, Path> watchKeys;
    private final List<PathMatcher> matchers;
    private final List<FileChangeListener> listeners;

    public DirectoryWatcher(String rootDirectory, String... globs) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.watchKeys = new HashMap<>();
        this.matchers = Arrays.stream(globs)
            .map(glob -> FileSystems.getDefault().getPathMatcher("glob:" + glob))
            .collect(Collectors.toList());
        this.listeners = new LinkedList<>();
        registerAll(Paths.get(rootDirectory));
    }

    public void register(FileChangeListener listener) {
        this.listeners.add(listener);
    }

    public void start() {
        while (true) {
            WatchKey watchKey;
            try {
                watchKey = watchService.take();
            } catch (InterruptedException exception) {
                log.debug("Interrupt received.", exception);
                return;
            }

            Path dir = watchKeys.get(watchKey);
            if (dir == null) {
                log.error("WatchKey not recognized: {}", watchKey);
                continue;
            }

            for (WatchEvent<?> event : watchKey.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == OVERFLOW) {
                    log.debug("Received OVERFLOW watch event, ignoring.");
                    continue;
                }

                WatchEvent<Path> watchEvent = cast(event);
                Path child = dir.resolve(watchEvent.context());

                if (matchers.stream().anyMatch(matcher -> matcher.matches(child))) {
                    listeners.forEach(listener -> listener.onFileChanged(child, FileChangeType.fromWatchEventKind(kind)));
                }

                if (kind == ENTRY_CREATE && Files.isDirectory(child, NOFOLLOW_LINKS)) {
                    registerAll(child);
                }
            }

            boolean valid = watchKey.reset();
            if (!valid) {
                watchKeys.remove(watchKey);
                if (watchKeys.isEmpty()) {
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    private void registerAll(final Path rootPath) {
        log.debug("Scanning path '{}'...", rootPath);
        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                    watchKeys.put(key, dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            log.debug("Finished scanning path '{}'", rootPath);
        } catch (IOException exception) {
            log.error("Failed to scan path '{}'", rootPath, exception);
        }
    }

}
