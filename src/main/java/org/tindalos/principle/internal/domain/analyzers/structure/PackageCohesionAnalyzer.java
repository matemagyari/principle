package org.tindalos.principle.internal.domain.analyzers.structure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.principle.internal.domain.analyzers.Analyzer;
import org.tindalos.principle.internal.domain.constraints.Constraints;
import org.tindalos.principle.internal.domain.plan.AnalysisInput;

/**
 * Analyzer producing package cohesion metrics and structure hints.
 */
public final class PackageCohesionAnalyzer implements Analyzer {

    @Override
    public CohesionAnalysisResult analyze(AnalysisInput input) {
        var nodes = input.nodes();

        var packagesWithCohesions = PackageCohesionModule.componentsFromPackages(input.analysisPlan().basePackage(), nodes);
        var structureHints1 = PackageStructureHints1Finder.makeGroups(nodes);
        var structureHints2 = Graph.findDetachableSubgraphs(nodes);

        Optional<Set<NodeGroup>> cohesiveGroups;
        if (input.packageCouplingExpectations().isPresent()) {
            var initialGroups = nodes.stream()
                    .map(node -> new NodeGroup(Set.of(node)))
                    .collect(Collectors.toUnmodifiableSet());
            cohesiveGroups = Optional.of(CohesiveGroupsDiscoveryModule.collapseToLimit(initialGroups));
        } else {
            cohesiveGroups = Optional.empty();
        }

        Map<String, NodeGroup> javaPackages = new LinkedHashMap<>();
        packagesWithCohesions.forEach(entry -> javaPackages.put(entry.getKey(), entry.getValue()));

        return new CohesionAnalysisResult(javaPackages, cohesiveGroups, structureHints1, structureHints2);
    }

    @Override
    public boolean isEnabled(Constraints constraints) {
        return constraints.packageCoupling().flatMap(pc -> pc.grouping()).isPresent();
    }
}