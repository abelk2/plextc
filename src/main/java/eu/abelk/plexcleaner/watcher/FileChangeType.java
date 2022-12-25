package eu.abelk.plexcleaner.watcher;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public enum FileChangeType {

    CREATE,
    MODIFY,
    DELETE;

    public static FileChangeType fromWatchEventKind(WatchEvent.Kind<?> watchEventKind) {
        FileChangeType result;
        if (watchEventKind == StandardWatchEventKinds.ENTRY_CREATE) {
            result = CREATE;
        } else if (watchEventKind == StandardWatchEventKinds.ENTRY_MODIFY) {
            result = MODIFY;
        } else if (watchEventKind == StandardWatchEventKinds.ENTRY_DELETE) {
            result = DELETE;
        } else {
            throw new IllegalArgumentException("Unsupported event kind: " + watchEventKind);
        }
        return result;
    }

}
