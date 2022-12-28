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
import java.util.stream.Collectors;

@Slf4j
public class TranscodedOriginalSeriesEpisodeReplacer {

    private static final Config CONFIG = ConfigHolder.getConfig();
    public static final String TRANSCODED_EPISODES_GLOB = "**/Plex Versions/" + CONFIG.plexVersionName() + "/[sS]??[eE]??.mp4";

    public void start() {
        try {
            processExisting();
            startWatcher();
        } catch (Throwable throwable) {
            log.error("Unexpected exception.", throwable);
        }
    }

    private void processExisting() {
        log.info("Processing existing transcoded series episodes...");
        DirectoryWalker walker = new DirectoryWalker(CONFIG.seriesRootDirectory(), TRANSCODED_EPISODES_GLOB);
        walker.walk(this::replaceMatchingOriginalFile);
        log.info("Finished processing existing transcoded series episodes.");
    }

    @SneakyThrows
    private void startWatcher() {
        log.info("Watching new transcoded series episodes...");
        DirectoryWatcher watcher = new DirectoryWatcher(CONFIG.seriesRootDirectory(), TRANSCODED_EPISODES_GLOB);
        watcher.register((transcodedFilePath, changeType) -> {
            if (changeType == FileChangeType.CREATE) {
                replaceMatchingOriginalFile(transcodedFilePath);
            }
        });
        watcher.start();
    }

    @SneakyThrows
    private void replaceMatchingOriginalFile(Path transcodedFilePath) {
        Path originalFileDirectory = transcodedFilePath.resolve("../../..").normalize();
        String transcodedFileBaseName = Util.getFileBaseName(transcodedFilePath);
        log.info("Found transcoded series episode\n\tLocation: {}\n\tOriginal dir: {}\n\tBase name: {}",
            transcodedFilePath, originalFileDirectory, transcodedFileBaseName);
        File[] files = originalFileDirectory.toFile()
            .listFiles((directory, name) -> Util.containsIgnoreCase(name, transcodedFileBaseName));
        if (files == null) {
            log.error("Listing files in {} failed.", originalFileDirectory);
        } else if (files.length < 1) {
            Path destination = originalFileDirectory.resolve(transcodedFilePath.getFileName());
            log.info("No matching episode in original directory. Moving file\n\tFrom: {}\n\tTo: {}",
                transcodedFilePath, destination);
            Files.move(transcodedFilePath, destination);
        } else if (files.length > 1) {
            String matches = Arrays.stream(files)
                .map(File::toString)
                .collect(Collectors.joining("\n\t"));
            log.error("Not moving anything, more than one original files match for base name {}; files: {}",
                transcodedFileBaseName, matches);
        } else {
            Path matchingFilePath = files[0].toPath();
            if (Util.isMp4(matchingFilePath)) {
                log.info("Found episode in original directory. Moving file\n\tFrom: {}\n\tTo: {}",
                    transcodedFilePath, matchingFilePath);
                Files.move(transcodedFilePath, matchingFilePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Path pathWithChangedExtension = originalFileDirectory.resolve(Util.getFileBaseName(matchingFilePath) + ".mp4");
                log.info("Found episode in original directory. Moving file (extension change needed)\n\tFrom: {}\n\tTo: {}",
                    transcodedFilePath, pathWithChangedExtension);
                Files.move(transcodedFilePath, pathWithChangedExtension, StandardCopyOption.REPLACE_EXISTING);
                Files.delete(matchingFilePath);
            }
        }
    }

}
