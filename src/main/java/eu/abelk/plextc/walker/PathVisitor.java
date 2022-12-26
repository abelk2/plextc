package eu.abelk.plextc.walker;

import java.nio.file.Path;

@FunctionalInterface
public interface PathVisitor {

    void visit(Path path);

}
