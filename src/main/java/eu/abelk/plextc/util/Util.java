package eu.abelk.plextc.util;

import lombok.extern.slf4j.Slf4j;

import static eu.abelk.plextc.util.Constants.VIDEO_FILE_EXTENSIONS;

@Slf4j
public class Util {

    private Util() {
    }

    public static boolean isVideoFile(String name) {
        boolean result = VIDEO_FILE_EXTENSIONS.stream()
            .anyMatch(extension -> name.endsWith("." + extension));
        log.debug("File {} {} a video file.", name, result ? "is" : "is not");
        return result;
    }

}
