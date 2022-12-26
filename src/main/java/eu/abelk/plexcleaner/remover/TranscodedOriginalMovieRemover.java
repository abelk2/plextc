package eu.abelk.plexcleaner.remover;

import eu.abelk.plexcleaner.util.Util;
import eu.abelk.plexcleaner.walker.DirectoryWalker;
import eu.abelk.plexcleaner.watcher.DirectoryWatcher;
import eu.abelk.plexcleaner.watcher.FileChangeType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import static eu.abelk.plexcleaner.util.Constants.*;

@Slf4j
public class TranscodedOriginalMovieRemover {

    public void start() {
        processExisting();
        startWatcher();
    }

    @SneakyThrows
    private void startWatcher(){
        log.info("Watching new transcoded movies...");
        DirectoryWatcher watcher = new DirectoryWatcher(ROOT_DIRECTORY, TRANSCODED_VIDEOS_GLOB);
        watcher.register((transcodedFilePath, changeType) -> {
            if (changeType == FileChangeType.CREATE) {
                removeOriginalFiles(transcodedFilePath);
            }
        });
        watcher.start();
    }

    private void processExisting() {
        log.info("Processing existing transcoded movies...");
        DirectoryWalker walker = new DirectoryWalker(ROOT_DIRECTORY, TRANSCODED_VIDEOS_GLOB);
        walker.walk(this::removeOriginalFiles);
        log.info("Finished processing existing transcoded movies.");
    }

    private void removeOriginalFiles(Path transcodedFilePath) {
        Path originalFileDirectory = transcodedFilePath.resolve("../../..").normalize();
        log.info("Found transcoded movie\n\tLocation: {}\n\tOriginal dir: {}", transcodedFilePath, originalFileDirectory);
        File[] files = originalFileDirectory.toFile()
            .listFiles((directory, name) -> Util.isVideoFile(name));
        if (files != null) {
            Arrays.stream(files)
                .forEach(file -> {
                    log.info("Deleting file\n\tLocation: {}", file);
                });
        }
    }

}
