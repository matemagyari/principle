package org.tindalos.principle.infrastructure.service.jdepend.classdependencies;

import java.util.Set;

import org.tindalos.principle.app.NodeBuilder;
import org.tindalos.principle.domain.analyzers.structure.Node;

/**
 * Default NodeBuilder implementation backed by JDepend class-file parsing.
 */
public class DefaultNodeBuilder implements NodeBuilder {

    @Override
    public Set<Node> build(String rootPackage) {
        return Set.copyOf(MyJDependRunner.createNodesOfClasses(rootPackage));
    }
}