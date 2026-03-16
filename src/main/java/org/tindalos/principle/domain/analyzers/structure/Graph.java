package org.tindalos.principle.domain.analyzers.structure;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility operations for navigating and decomposing dependency graphs.
 */
public final class Graph {

    private Graph() {
    }

    public static boolean isValid(Set<Node> graph) {
        for (var first : graph) {
            for (var second : graph) {
                if (!first.equals(second) && !symmetryHolds(first, second)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isIsland(Set<Node> subgraph) {
        var subgraphIds = subgraph.stream().map(Node::id).collect(Collectors.toSet());
        var externalDependencies = subgraph.stream()
                .flatMap(node -> node.dependants().stream())
                .filter(id -> !subgraphIds.contains(id))
                .collect(Collectors.toSet());
        return externalDependencies.isEmpty();
    }

    public static Set<Node> findDownstreamNodes(Node node, Set<Node> graph) {
        var nodeMap = graph.stream().collect(Collectors.toMap(Node::id, n -> n, (first, second) -> first));
        return findDownstreamNodes(node, Set.of(), nodeMap);
    }

    public static Set<Node> findSources(Set<Node> graph) {
        return graph.stream()
                .filter(node -> node.dependants().isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    public static SubgraphDecomposition findDetachableSubgraphs(Set<Node> graph) {
        var frontNodesBySubgraph = new HashMap<Set<Node>, Set<Node>>();

        for (var node : graph) {
            var subgraph = findDetachableSubgraph(node, graph);
            if (subgraph.size() > 1) {
                frontNodesBySubgraph.computeIfAbsent(subgraph, ignored -> new HashSet<>()).add(node);
            }
        }

        var peninsulas = frontNodesBySubgraph.entrySet().stream()
                .map(entry -> new Peninsula(entry.getValue(), entry.getKey()))
                .sorted(Comparator.comparingInt((Peninsula peninsula) -> peninsula.subgraph().size()).reversed())
                .toList();

        return new SubgraphDecomposition(peninsulas);
    }

    private static boolean symmetryHolds(Node first, Node second) {
        if (first.dependencies().contains(second.id())) {
            return second.dependants().contains(first.id());
        }
        if (first.dependants().contains(second.id())) {
            return second.dependencies().contains(first.id());
        }
        return true;
    }

    private static Set<Node> findDownstreamNodes(Node node, Set<Node> acc, Map<String, Node> nodeMap) {
        var newAcc = new HashSet<>(acc);
        newAcc.add(node);

        var nextNodes = node.dependencies().stream()
                .map(nodeMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        nextNodes.removeAll(newAcc);

        var result = new HashSet<>(newAcc);
        var nextAcc = new HashSet<>(newAcc);
        nextAcc.addAll(nextNodes);
        for (var nextNode : nextNodes) {
            result.addAll(findDownstreamNodes(nextNode, nextAcc, nodeMap));
        }
        return Set.copyOf(result);
    }

    private static Set<Node> findDetachableSubgraph(Node node, Set<Node> graph) {
        return findDetachableSubgraph(node, Set.of(), graph);
    }

    private static Set<Node> findDetachableSubgraph(Node startNode, Set<Node> upstreamNodes, Set<Node> graph) {
        var result = new HashSet<Node>();
        var upstreamIds = upstreamNodes.stream().map(Node::id).collect(Collectors.toSet());

        for (var node : graph) {
            var externalUpstreams = new HashSet<>(node.dependants());
            externalUpstreams.remove(startNode.id());
            externalUpstreams.removeAll(upstreamIds);

            if (startNode.dependencies().contains(node.id())
                    && externalUpstreams.isEmpty()
                    && !upstreamNodes.contains(node)) {
                result.addAll(findDetachableSubgraph(node, append(upstreamNodes, startNode), graph));
            }
        }

        var combined = new HashSet<>(upstreamNodes);
        combined.add(startNode);
        combined.addAll(result);
        return Set.copyOf(combined);
    }

    private static Set<Node> append(Set<Node> nodes, Node node) {
        var result = new HashSet<>(nodes);
        result.add(node);
        return Set.copyOf(result);
    }
}