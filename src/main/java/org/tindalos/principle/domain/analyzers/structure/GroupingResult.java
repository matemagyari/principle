package org.tindalos.principle.domain.analyzers.structure;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the result of grouping nodes by their source dependencies.
 *
 * @param grouping        maps a set of source labels to the node IDs in that group
 * @param labelledSources ordered list pairing each source label with its node ID
 */
public record GroupingResult(
        Map<Set<String>, List<String>> grouping,
        List<LabelledSource> labelledSources) {

    public GroupingResult {
        Map<Set<String>, List<String>> copy = new LinkedHashMap<>();
        grouping.forEach((k, v) -> copy.put(Set.copyOf(k), List.copyOf(v)));
        grouping = Collections.unmodifiableMap(copy);
        labelledSources = List.copyOf(labelledSources);
    }

    /**
     * A source node annotated with a short label (e.g. "s01").
     *
     * @param label  short label assigned to the source
     * @param nodeId the node's identifier
     */
    public record LabelledSource(String label, String nodeId) {}
}
