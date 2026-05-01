package org.tindalos.guardrails.internal.app;

import java.util.Set;

import org.tindalos.guardrails.internal.domain.core.Node;

/**
 * Builds analyzed class dependency graph nodes for a given root package.
 */
public interface NodeBuilder {

    Set<Node> build(String rootPackage);
}