package org.tindalos.guardrails.internal.domain.core;

import java.util.Set;

public record Node(String id, Set<String> dependencies, Set<String> dependants) {
    public Node {
        dependencies = Set.copyOf(dependencies);
        dependants = Set.copyOf(dependants);
    }
}
