package eu.abelk.plextc.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Locale;

@Slf4j
public class Util {

    private static final Config CONFIG = ConfigHolder.getConfig();

    private Util() {
    }

    public static boolean isVideoFile(String name) {
        boolean result = CONFIG.videoFileExtensions()
            .stream()
            .anyMatch(extension -> name.endsWith("." + extension));
        log.debug("File {} {} a video file.", name, result ? "is" : "is not");
        return result;
    }

    public static String getFileBaseName(Path transcodedFilePath) {
        return transcodedFilePath.getFileName()
            .toString()
            .replaceFirst("[.][^.]+$", "");
    }

    public static boolean containsIgnoreCase(String container, String contained) {
        return container.toLowerCase(Locale.ROOT)
            .contains(contained.toLowerCase(Locale.ROOT));
    }

}
