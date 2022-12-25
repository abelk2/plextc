package eu.abelk.plexcleaner.watcher;

import java.nio.file.Path;

@FunctionalInterface
public interface FileChangeListener {

    void onFileChanged(Path filePath, FileChangeType changeType);

}
