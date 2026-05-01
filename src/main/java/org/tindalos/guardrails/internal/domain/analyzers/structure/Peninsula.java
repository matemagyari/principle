package org.tindalos.guardrails.internal.domain.analyzers.structure;

import org.tindalos.guardrails.internal.domain.core.Node;

import java.util.Set;
import java.util.stream.Collectors;

public record Peninsula(Set<Node> frontNodes, Set<Node> subgraph, boolean island) {

    public Peninsula(Set<Node> frontNodes, Set<Node> subgraph) {
        this(frontNodes, subgraph, computeIsland(subgraph));
    }

    public Peninsula {
        frontNodes = Set.copyOf(frontNodes);
        subgraph = Set.copyOf(subgraph);
        if (!subgraph.containsAll(frontNodes)) {
            throw new IllegalArgumentException("frontNodes must be a subset of subgraph");
        }
    }

    // Preserves the original Scala logic which used dependants twice (not dependencies+dependants)
    private static boolean computeIsland(Set<Node> subgraph) {
        var subgraphIds = subgraph.stream().map(Node::id).collect(Collectors.toSet());
        var externalDependencies = subgraph.stream()
                .flatMap(n -> n.dependants().stream())
                .filter(id -> !subgraphIds.contains(id))
                .collect(Collectors.toSet());
        return externalDependencies.isEmpty();
    }
}
