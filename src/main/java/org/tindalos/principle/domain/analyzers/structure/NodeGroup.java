package org.tindalos.principle.domain.analyzers.structure;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a group of nodes (classes) and computes cohesion metrics for the group.
 */
public final class NodeGroup {

    private final Set<Node> nodes;
    public final Set<String> externalDependencies;
    public final Set<String> externalDependants;
    public final int externalGroupConnectionsNo;
    public final int internalArcsNo;
    public final int externalArcsNo;
    public final Map.Entry<String, Double> leastBelongingNode;
    private final double generalCohesion;

    public NodeGroup(Set<Node> nodes) {
        this.nodes = Set.copyOf(nodes);

        var nodeIds = this.nodes.stream().map(Node::id).collect(Collectors.toUnmodifiableSet());

        this.externalDependencies = this.nodes.stream()
                .flatMap(n -> n.dependencies().stream())
                .filter(dep -> !nodeIds.contains(dep))
                .collect(Collectors.toUnmodifiableSet());

        this.externalDependants = this.nodes.stream()
                .flatMap(n -> n.dependants().stream())
                .filter(dep -> !nodeIds.contains(dep))
                .collect(Collectors.toUnmodifiableSet());

        this.externalGroupConnectionsNo = externalDependencies.size() + externalDependants.size();

        var allEdges = this.nodes.stream()
                .flatMap(n -> Stream.concat(n.dependants().stream(), n.dependencies().stream()))
                .collect(Collectors.toList());
        long internals = allEdges.stream().filter(nodeIds::contains).count();
        long externals = allEdges.stream().filter(id -> !nodeIds.contains(id)).count();
        this.internalArcsNo = (int) (internals / 2);
        this.externalArcsNo = (int) externals;

        int arcsNo = internalArcsNo + externalArcsNo;
        this.generalCohesion = arcsNo == 0 ? 0.0 : 1.0 - (double) externalGroupConnectionsNo / (double) arcsNo;

        this.leastBelongingNode = this.nodes.stream()
                .map(n -> Map.entry(n.id(), nodeBelongingness(n)))
                .min(Map.Entry.comparingByValue())
                .orElseThrow(() -> new IllegalArgumentException("NodeGroup must not be empty"));
    }

    public Set<Node> nodes() {
        return nodes;
    }

    public double cohesion() {
        return generalCohesion;
    }

    public double nodeBelongingness(Node n) {
        int total = internalArcsNo + externalArcsNo;
        if (total == 0) return 0.0;
        return (double) (n.dependants().size() + n.dependencies().size()) / (double) total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeGroup other)) return false;
        return nodes.equals(other.nodes);
    }

    @Override
    public int hashCode() {
        return nodes.hashCode();
    }

    @Override
    public String toString() {
        return nodes.stream().map(n -> "," + n.id()).collect(Collectors.joining());
    }
}
