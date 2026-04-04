package org.tindalos.principle.domain.analyzers.structure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.principle.domain.plan.AnalysisInput;
import org.tindalos.principle.domain.analyzers.Analyzer;
import org.tindalos.principle.domain.constraints.Constraints;

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
    public boolean isEnabled(Constraints expectations) {
        return expectations.packageCoupling().isPresent()
                && expectations.packageCoupling().get().grouping().isPresent();
    }
}