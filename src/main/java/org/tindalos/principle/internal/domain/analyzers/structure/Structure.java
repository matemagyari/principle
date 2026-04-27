package org.tindalos.principle.internal.domain.analyzers.structure;

import java.util.HashSet;
import java.util.stream.Collectors;

import org.tindalos.principle.internal.domain.core.Node;

/**
 * Utility methods for computing relationships and metrics between NodeGroups.
 */
public final class Structure {

    private Structure() {}

    public static NodeGroup merge(NodeGroup n1, NodeGroup n2) {
        var combined = new HashSet<>(n1.nodes());
        combined.addAll(n2.nodes());
        return new NodeGroup(combined);
    }

    public static double cohesionDelta(NodeGroup n1, NodeGroup n2) {
        double mergedCohesion = merge(n1, n2).cohesion();
        return Math.min(mergedCohesion - n1.cohesion(), mergedCohesion - n2.cohesion());
    }

    public static double commonDependenciesRatio(NodeGroup n1, NodeGroup n2) {
        if (n1.externalDependencies.isEmpty()) {
            return n2.externalDependencies.isEmpty() ? 1.0 : 0.0;
        }
        long commonCount = n1.externalDependencies.stream()
            .filter(n2.externalDependencies::contains)
            .count();
        return (double) commonCount / n1.externalDependencies.size();
    }

    public static double commonDependantsRatio(NodeGroup n1, NodeGroup n2) {
        if (n1.externalDependants.isEmpty()) {
            return n2.externalDependants.isEmpty() ? 1.0 : 0.0;
        }
        long commonCount = n1.externalDependants.stream()
            .filter(n2.externalDependants::contains)
            .count();
        return (double) commonCount / n1.externalDependants.size();
    }

    public static double connectionRate(NodeGroup n1, NodeGroup n2) {
        var n2ids = n2.nodes().stream().map(Node::id).collect(Collectors.toSet());
        long commonCount = n1.externalDependencies.stream()
            .filter(n2ids::contains)
            .count();
        return (double) commonCount / (double) n2.nodes().size();
    }

    public static boolean isIsolated(NodeGroup n) {
        return n.externalDependencies.isEmpty() && n.externalDependants.isEmpty();
    }

    public static double gravityTo(NodeGroup n1, NodeGroup n2) {
        if (isIsolated(n1) && isIsolated(n2)) return 0.0;
        if (isConnectedTo(n1, n2) || isConnectedTo(n2, n1))
            return commonDependenciesRatio(n1, n2) * commonDependantsRatio(n1, n2);
        return 0.0;
    }

    public static double gravityBetween(NodeGroup n1, NodeGroup n2) {
        return gravityTo(n1, n2) * gravityTo(n2, n1);
    }

    public static boolean isConnectedTo(NodeGroup n1, NodeGroup n2) {
        var n2ids = n2.nodes().stream().map(Node::id).collect(Collectors.toSet());
        return n1.externalDependencies.stream()
            .anyMatch(n2ids::contains);
    }
}
