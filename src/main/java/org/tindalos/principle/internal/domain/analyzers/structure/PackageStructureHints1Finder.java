package org.tindalos.principle.internal.domain.analyzers.structure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.principle.internal.domain.analyzers.structure.GroupingResult.LabelledSource;
import org.tindalos.principle.internal.domain.core.Node;

/**
 * Creates source-based grouping hints from a class dependency graph.
 */
public final class PackageStructureHints1Finder {

    private PackageStructureHints1Finder() {
    }

    public static GroupingResult makeGroups(Set<Node> graph) {
        var sources = Graph.findSources(graph).stream()
                .sorted(Comparator.comparing(Node::id))
                .toList();

        var labelledSources = new ArrayList<Map.Entry<Node, String>>();
        for (int i = 0; i < sources.size(); i++) {
            labelledSources.add(Map.entry(sources.get(i), label(sources.size(), i)));
        }

        var groupedLabelsByNode = new HashMap<String, Set<String>>();
        for (var labelledSource : labelledSources) {
            var source = labelledSource.getKey();
            var sourceLabel = labelledSource.getValue();
            for (var downstream : Graph.findDownstreamNodes(source, graph)) {
                groupedLabelsByNode.merge(
                        downstream.id(),
                        Set.of(sourceLabel),
                        (existing, added) -> {
                            var merged = new java.util.HashSet<>(existing);
                            merged.addAll(added);
                            return Set.copyOf(merged);
                        });
            }
        }

        var groupedNodeIdsBySourceSet = new LinkedHashMap<Set<String>, List<String>>();
        for (var entry : groupedLabelsByNode.entrySet()) {
            groupedNodeIdsBySourceSet
                    .computeIfAbsent(entry.getValue(), ignored -> new ArrayList<>())
                    .add(entry.getKey());
        }

        var javaGrouping = groupedNodeIdsBySourceSet.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue()),
                        (left, right) -> left,
                        LinkedHashMap::new));

        var javaLabelledSources = labelledSources.stream()
                .map(entry -> new LabelledSource(entry.getValue(), entry.getKey().id()))
                .toList();

        return new GroupingResult(javaGrouping, javaLabelledSources);
    }

    public static String label(int max, int i) {
        String postfix;
        if (max < 10) {
            postfix = Integer.toString(i);
        } else if (max < 100) {
            postfix = i < 10 ? "0" + i : Integer.toString(i);
        } else if (max < 1000) {
            if (i < 10) {
                postfix = "00" + i;
            } else {
                postfix = i < 100 ? "0" + i : Integer.toString(i);
            }
        } else {
            postfix = Integer.toString(i);
        }
        return "s" + postfix;
    }
}