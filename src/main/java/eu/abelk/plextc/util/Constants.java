package eu.abelk.plextc.util;

import java.util.Set;

public class Constants {

    public static final Set<String> VIDEO_FILE_EXTENSIONS = Set.of("mkv", "mp4", "avi");
    public static final String ROOT_DIRECTORY = "D:\\Users\\dementor\\Desktop\\plex-test\\";
    public static final String TRANSCODED_VIDEOS_GLOB = "**/Plex Versions/Optimized for TV/?*.{" + String.join(",", VIDEO_FILE_EXTENSIONS) + "}";

    private Constants() {
    }

}
