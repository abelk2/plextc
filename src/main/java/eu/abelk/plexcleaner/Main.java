package eu.abelk.plexcleaner;

import eu.abelk.plexcleaner.watcher.DirectoryWatcher;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        DirectoryWatcher watcher = new DirectoryWatcher(".", "**/*.txt");
        watcher.register((filePath, changeType) -> {
            System.out.format("%s: %s\n", changeType, filePath.toString());
        });
        watcher.start();
    }

}
