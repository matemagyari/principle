package org.tindalos.principle.domain.analyzers.structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Collapses node groups greedily while cohesion gain stays above threshold.
 */
public final class CohesiveGroupsDiscoveryModule {

    private static final double COHESION_DELTA_LIMIT = 0.1;

    private CohesiveGroupsDiscoveryModule() {
    }

    public static Set<NodeGroup> collapseToLimit(Set<NodeGroup> initialComponents) {
        var pairs = new HashMap<NodeGroupPair, Double>();
        var asList = initialComponents.stream().toList();

        for (var n1 : asList) {
            for (var n2 : asList) {
                if (!n1.equals(n2) && n1.hashCode() <= n2.hashCode()) {
                    var pair = pairOf(n1, n2);
                    pairs.put(pair, pair.delta());
                }
            }
        }

        return collapse(initialComponents, pairs);
    }

    private static Set<NodeGroup> collapse(Set<NodeGroup> groups, Map<NodeGroupPair, Double> pairMap) {
        var currentGroups = Set.copyOf(groups);
        var currentPairs = new HashMap<>(pairMap);

        while (true) {
            var max = currentPairs.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow();

            if (max.getValue() <= COHESION_DELTA_LIMIT) {
                return currentGroups;
            }

            var left = max.getKey().first();
            var right = max.getKey().second();
            var merged = Structure.merge(left, right);

            var updatedGroups = new HashSet<>(currentGroups);
            updatedGroups.remove(left);
            updatedGroups.remove(right);
            updatedGroups.add(merged);

            var updatedPairs = new HashMap<NodeGroupPair, Double>();
            currentPairs.forEach((pair, delta) -> {
                if (!overlap(pair, left, right)) {
                    updatedPairs.put(pair, delta);
                }
            });

            for (var group : updatedGroups) {
                if (!group.equals(merged)) {
                    var pair = pairOf(group, merged);
                    updatedPairs.put(pair, pair.delta());
                }
            }

            currentGroups = Set.copyOf(updatedGroups);
            currentPairs = updatedPairs;
        }
    }

    private static boolean overlap(NodeGroupPair pair, NodeGroup left, NodeGroup right) {
        var distinct = Set.of(pair.first(), pair.second(), left, right);
        return distinct.size() < 4;
    }

    private static NodeGroupPair pairOf(NodeGroup n1, NodeGroup n2) {
        return new NodeGroupPair(n1, n2, Structure.cohesionDelta(n1, n2));
    }

    private record NodeGroupPair(NodeGroup first, NodeGroup second, double delta) {
        private NodeGroupPair {
            Objects.requireNonNull(first, "first");
            Objects.requireNonNull(second, "second");
        }
    }
}