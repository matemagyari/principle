package org.tindalos.principle.app;

import java.util.Set;

import org.tindalos.principle.domain.analyzers.structure.Node;

/**
 * Builds analyzed class dependency graph nodes for a given root package.
 */
public interface NodeBuilder {

    Set<Node> build(String rootPackage);
}