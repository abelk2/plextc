package eu.abelk.plextc.remover;

import eu.abelk.plextc.util.Config;
import eu.abelk.plextc.util.ConfigHolder;
import eu.abelk.plextc.util.Util;
import eu.abelk.plextc.walker.DirectoryWalker;
import eu.abelk.plextc.watcher.DirectoryWatcher;
import eu.abelk.plextc.watcher.FileChangeType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

@Slf4j
public class TranscodedOriginalMovieRemover {

    private static final Config CONFIG = ConfigHolder.getConfig();
    public static final String TRANSCODED_MOVIES_GLOB = "**/Plex Versions/" + CONFIG.plexVersionName()
        + "/?*.{" + String.join(",", CONFIG.videoFileExtensions()) + "}";

    public void start() {
        processExisting();
        startWatcher();
    }

    private void processExisting() {
        log.info("Processing existing transcoded movies...");
        DirectoryWalker walker = new DirectoryWalker(CONFIG.moviesRootDirectory(), TRANSCODED_MOVIES_GLOB);
        walker.walk(this::removeOriginalFiles);
        log.info("Finished processing existing transcoded movies.");
    }

    @SneakyThrows
    private void startWatcher() {
        log.info("Watching new transcoded movies...");
        DirectoryWatcher watcher = new DirectoryWatcher(CONFIG.moviesRootDirectory(), TRANSCODED_MOVIES_GLOB);
        watcher.register((transcodedFilePath, changeType) -> {
            if (changeType == FileChangeType.CREATE) {
                removeOriginalFiles(transcodedFilePath);
            }
        });
        watcher.start();
    }

    private void removeOriginalFiles(Path transcodedFilePath) {
        Path originalFileDirectory = transcodedFilePath.resolve("../../..").normalize();
        log.info("Found transcoded movie\n\tLocation: {}\n\tOriginal dir: {}", transcodedFilePath, originalFileDirectory);
        File[] files = originalFileDirectory.toFile()
            .listFiles((directory, name) -> Util.isVideoFile(name));
        if (files == null) {
            log.error("Listing files in {} failed.", originalFileDirectory);
        } else {
            Arrays.stream(files)
                .forEach(file -> {
                    log.info("Deleting file\n\tLocation: {}", file);
                    boolean successful = file.delete();
                    if (!successful) {
                        log.error("Failed to delete file\n\tLocation: {}", file);
                    }
                });
        }
    }

}
