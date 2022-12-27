package eu.abelk.plextc.replacer;

import eu.abelk.plextc.util.Config;
import eu.abelk.plextc.util.ConfigHolder;
import eu.abelk.plextc.util.Util;
import eu.abelk.plextc.walker.DirectoryWalker;
import eu.abelk.plextc.watcher.DirectoryWatcher;
import eu.abelk.plextc.watcher.FileChangeType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

@Slf4j
public class TranscodedOriginalMovieReplacer {

    private static final Config CONFIG = ConfigHolder.getConfig();
    public static final String TRANSCODED_MOVIES_GLOB = "**/Plex Versions/" + CONFIG.plexVersionName()
        + "/?*.{" + String.join(",", CONFIG.videoFileExtensions()) + "}";

    public void start() {
        try {
            processExisting();
            startWatcher();
        } catch (Throwable throwable) {
            log.error("Unexpected exception.", throwable);
        }
    }

    private void processExisting() {
        log.info("Processing existing transcoded movies...");
        DirectoryWalker walker = new DirectoryWalker(CONFIG.moviesRootDirectory(), TRANSCODED_MOVIES_GLOB);
        walker.walk(this::replaceOrRemoveOriginalFiles);
        log.info("Finished processing existing transcoded movies.");
    }

    @SneakyThrows
    private void startWatcher() {
        log.info("Watching new transcoded movies...");
        DirectoryWatcher watcher = new DirectoryWatcher(CONFIG.moviesRootDirectory(), TRANSCODED_MOVIES_GLOB);
        watcher.register((transcodedFilePath, changeType) -> {
            if (changeType == FileChangeType.CREATE) {
                replaceOrRemoveOriginalFiles(transcodedFilePath);
            }
        });
        watcher.start();
    }

    @SneakyThrows
    private void replaceOrRemoveOriginalFiles(Path transcodedFilePath) {
        Path originalFileDirectory = transcodedFilePath.resolve("../../..").normalize();
        log.info("Found transcoded movie\n\tLocation: {}\n\tOriginal dir: {}", transcodedFilePath, originalFileDirectory);
        File[] files = originalFileDirectory.toFile()
            .listFiles((directory, name) -> Util.isVideoFile(name));
        if (files == null) {
            log.error("Listing files in {} failed.", originalFileDirectory);
        } else if (files.length < 1) {
            Path destination = originalFileDirectory.resolve(transcodedFilePath.getFileName());
            log.info("No movies in original directory. Moving file\n\tFrom: {}\n\tTo: {}",
                transcodedFilePath, destination);
            Files.move(transcodedFilePath, destination);
        } else {
            File firstFile = files[0];
            log.info("Found movies in original directory. Moving file\n\tFrom: {}\n\tTo: {}",
                transcodedFilePath, firstFile.toPath());
            Files.move(transcodedFilePath, firstFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Arrays.stream(files)
                .skip(1)
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
