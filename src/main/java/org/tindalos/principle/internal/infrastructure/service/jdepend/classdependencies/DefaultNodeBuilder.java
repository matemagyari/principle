package org.tindalos.principle.internal.infrastructure.service.jdepend.classdependencies;

import java.util.Set;

import org.tindalos.principle.internal.app.NodeBuilder;
import org.tindalos.principle.internal.domain.core.Node;

/**
 * Default NodeBuilder implementation backed by JDepend class-file parsing.
 */
public class DefaultNodeBuilder implements NodeBuilder {

    @Override
    public Set<Node> build(String rootPackage) {
        return Set.copyOf(MyJDependRunner.createNodesOfClasses(rootPackage));
    }
}