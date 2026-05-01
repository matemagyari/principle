package org.tindalos.guardrails.internal.domain.analyzers.structure;

import org.tindalos.guardrails.internal.domain.core.AnalysisResult;

import java.util.*;

/**
 * Holds the full result of a package cohesion analysis run.
 *
 * @param packages              map of package name to its NodeGroup (cohesion metrics)
 * @param cohesiveNodeGroups    detected cohesive groups, present only when grouping was requested
 * @param groupingResult        source-based structural grouping hints
 * @param subgraphDecomposition detachable subgraph decomposition of the dependency graph
 */
public record CohesionAnalysisResult(
        Map<String, NodeGroup> packages,
        Optional<Set<NodeGroup>> cohesiveNodeGroups,
        GroupingResult groupingResult,
        SubgraphDecomposition subgraphDecomposition) implements AnalysisResult {

    public CohesionAnalysisResult {
        packages = Collections.unmodifiableMap(new LinkedHashMap<>(packages));
        cohesiveNodeGroups = cohesiveNodeGroups.map(groups -> Collections.unmodifiableSet(new HashSet<>(groups)));
    }

    @Override
    public boolean constraintViolated() {
        return false;
    }
}
