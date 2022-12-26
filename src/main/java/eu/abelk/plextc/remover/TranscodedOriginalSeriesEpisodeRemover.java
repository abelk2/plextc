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
import java.util.stream.Collectors;

@Slf4j
public class TranscodedOriginalSeriesEpisodeRemover {

    private static final Config CONFIG = ConfigHolder.getConfig();
    public static final String TRANSCODED_EPISODES_GLOB = "**/Plex Versions/" + CONFIG.plexVersionName()
        + "/S??E??.{" + String.join(",", CONFIG.videoFileExtensions()) + "}";

    public void start() {
        processExisting();
        startWatcher();
    }

    private void processExisting() {
        log.info("Processing existing transcoded series episodes...");
        DirectoryWalker walker = new DirectoryWalker(CONFIG.seriesRootDirectory(), TRANSCODED_EPISODES_GLOB);
        walker.walk(this::removeMatchingOriginalFile);
        log.info("Finished processing existing transcoded series episodes.");
    }

    @SneakyThrows
    private void startWatcher() {
        log.info("Watching new transcoded series episodes...");
        DirectoryWatcher watcher = new DirectoryWatcher(CONFIG.seriesRootDirectory(), TRANSCODED_EPISODES_GLOB);
        watcher.register((transcodedFilePath, changeType) -> {
            if (changeType == FileChangeType.CREATE) {
                removeMatchingOriginalFile(transcodedFilePath);
            }
        });
        watcher.start();
    }

    private void removeMatchingOriginalFile(Path transcodedFilePath) {
        Path originalFileDirectory = transcodedFilePath.resolve("../../..").normalize();
        String transcodedFileBaseName = Util.getFileBaseName(transcodedFilePath);
        log.info("Found transcoded series episode\n\tLocation: {}\n\tOriginal dir: {}\n\tBase name: {}",
            transcodedFilePath, originalFileDirectory, transcodedFileBaseName);
        File[] files = originalFileDirectory.toFile()
            .listFiles((directory, name) -> Util.containsIgnoreCase(name, transcodedFileBaseName));
        if (files == null) {
            log.error("Listing files in {} failed.", originalFileDirectory);
        } else {
            if (files.length > 1) {
                String matches = Arrays.stream(files)
                    .map(File::toString)
                    .collect(Collectors.joining("\n\t"));
                log.error("More than one original files match for base name {}; files: {}", transcodedFileBaseName, matches);
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

}
