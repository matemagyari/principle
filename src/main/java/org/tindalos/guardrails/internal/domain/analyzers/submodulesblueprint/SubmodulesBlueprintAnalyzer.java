package org.tindalos.guardrails.internal.domain.analyzers.submodulesblueprint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tindalos.guardrails.internal.domain.analyzers.Analyzer;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.submodules.OverlappingSubmoduleDefinitionsException;
import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;

/**
 * Analyzes discovered packages against declared submodule blueprint definitions.
 */
public class SubmodulesBlueprintAnalyzer implements Analyzer {

    private final SubmodulesBuilder submodulesBuilder;

    public SubmodulesBlueprintAnalyzer(SubmodulesBuilder submodulesBuilder) {
        this.submodulesBuilder = submodulesBuilder;
    }

    @Override
    public boolean isEnabled(Constraints designQualityConstraints) {
        return designQualityConstraints.submoduleDefinitions().isPresent();
    }

    @Override
    public SubmodulesBlueprintAnalysisResult analyze(AnalysisInput checkInput) {
        if (checkInput.submoduleDefinitions().isEmpty()) {
            return SubmodulesBlueprintAnalysisResult.empty(0);
        }

        var submoduleDefinitions = checkInput.submoduleDefinitions().orElseThrow();
        try {
            Set<Submodule> submodules = Set.copyOf(submodulesBuilder.build(
                submoduleDefinitions,
                checkInput.packages(),
                checkInput.analysisPlan().basePackage()));

            var dependencyMaps = problematicDependencies(submodules);
            return SubmodulesBlueprintAnalysisResult.withViolations(
                submoduleDefinitions.violationThreshold(),
                dependencyMaps.illegalDependencies(),
                dependencyMaps.missingDependencies());
        } catch (OverlappingSubmoduleDefinitionsException ex) {
            return SubmodulesBlueprintAnalysisResult.withOverlaps(
                submoduleDefinitions.violationThreshold(),
                ex.getOverlaps());
        }
    }

    private DependencyMaps problematicDependencies(Set<Submodule> submodules) {
        Map<Submodule, Set<Submodule>> illegalDependenciesBySubmodule = new HashMap<>();
        Map<Submodule, Set<Submodule>> missingDependenciesBySubmodule = new HashMap<>();

        for (Submodule submodule : submodules) {
            Set<Submodule> otherSubmodules = new HashSet<>(submodules);
            otherSubmodules.remove(submodule);

            Set<Submodule> illegalDependencies = Set.copyOf(submodule.findIllegalDependencies(otherSubmodules));
            Set<Submodule> missingDependencies = Set.copyOf(submodule.findMissingPredefinedDependencies(otherSubmodules));

            if (!illegalDependencies.isEmpty()) {
                illegalDependenciesBySubmodule.put(submodule, illegalDependencies);
            }
            if (!missingDependencies.isEmpty()) {
                missingDependenciesBySubmodule.put(submodule, missingDependencies);
            }
        }

        return new DependencyMaps(Map.copyOf(illegalDependenciesBySubmodule), Map.copyOf(missingDependenciesBySubmodule));
    }

    /**
     * Internal immutable pair of dependency maps.
     */
    private record DependencyMaps(
        Map<Submodule, Set<Submodule>> illegalDependencies,
        Map<Submodule, Set<Submodule>> missingDependencies) {
    }
}