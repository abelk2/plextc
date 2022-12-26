package eu.abelk.plextc.walker;

import lombok.SneakyThrows;

import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryWalker {

    private final List<PathMatcher> matchers;
    private final Path rootPath;

    public DirectoryWalker(String rootDirectory, String... globs) {
        this.matchers = Arrays.stream(globs)
            .map(glob -> FileSystems.getDefault().getPathMatcher("glob:" + glob))
            .collect(Collectors.toList());
        this.rootPath = Paths.get(rootDirectory);
    }

    @SneakyThrows
    public void walk(PathVisitor fileVisitor) {
        Set<Path> mathingPaths;
        try (Stream<Path> stream = Files.walk(rootPath)) {
            mathingPaths = stream.filter(this::matchesAnyGlob)
                .collect(Collectors.toSet());
        }
        mathingPaths.stream()
            .filter(path -> path.toFile().exists())
            .forEach(fileVisitor::visit);
    }

    private boolean matchesAnyGlob(Path path) {
        return matchers.stream()
            .anyMatch(matcher -> matcher.matches(path));
    }

}
