package eu.abelk.plexcleaner.walker;

import java.nio.file.Path;

@FunctionalInterface
public interface PathVisitor {

    void visit(Path path);

}
