package org.tindalos.principle.domain.analyzers.structure;

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

    /**
     * A source node annotated with a short label (e.g. "s01").
     *
     * @param label  short label assigned to the source
     * @param nodeId the node's identifier
     */
    public record LabelledSource(String label, String nodeId) {}
}
