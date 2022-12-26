package eu.abelk.plextc.walker;

import lombok.SneakyThrows;

import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
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
        try (Stream<Path> stream = Files.walk(rootPath)) {
            stream.filter(this::matchesAnyGlob)
                .forEach(fileVisitor::visit);
        }
    }

    private boolean matchesAnyGlob(Path path) {
        return matchers.stream()
            .anyMatch(matcher -> matcher.matches(path));
    }

}
