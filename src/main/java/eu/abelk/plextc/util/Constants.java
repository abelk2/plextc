package eu.abelk.plextc.util;

import java.util.Set;

public class Constants {

    public static final Set<String> VIDEO_FILE_EXTENSIONS = Set.of("mkv", "mp4", "avi");
    public static final String TRANSCODED_MOVIES_GLOB = "**/Plex Versions/Optimized for TV/?*.{" + String.join(",", VIDEO_FILE_EXTENSIONS) + "}";
    public static final String TRANSCODED_EPISODES_GLOB = "**/Plex Versions/Optimized for TV/S??E??.{" + String.join(",", VIDEO_FILE_EXTENSIONS) + "}";

    public static final String ROOT_DIRECTORY = "D:\\Users\\dementor\\Desktop\\plex-test\\";
    public static final String ROOT_DIRECTORY_SERIES = "D:\\Users\\dementor\\Desktop\\plex-test-series\\";

    private Constants() {
    }

}
