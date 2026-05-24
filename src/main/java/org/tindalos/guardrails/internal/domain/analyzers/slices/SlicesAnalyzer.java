package org.tindalos.guardrails.internal.domain.analyzers.slices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.guardrails.internal.domain.analyzers.Analyzer;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.slices.OverlappingSliceDefinitionsException;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceGroup;
import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;

/**
 * Analyzes discovered package structures against arbitrary slice definitions and boundaries.
 */
public class SlicesAnalyzer implements Analyzer {

    private final SlicesBuilder slicesBuilder;

    public SlicesAnalyzer(SlicesBuilder slicesBuilder) {
        this.slicesBuilder = slicesBuilder;
    }

    @Override
    public boolean isEnabled(Constraints designQualityConstraints) {
        return designQualityConstraints.slices().isPresent();
    }

    @Override
    public SlicesAnalysisResult analyze(AnalysisInput checkInput) {
        if (checkInput.slices().isEmpty()) {
            return new SlicesAnalysisResult(List.of());
        }

        var slicesConstraint = checkInput.slices().orElseThrow();
        List<SliceGroupResult> groupResults = new ArrayList<>();

        for (SliceGroup sliceGroup : slicesConstraint.sliceGroups()) {
            try {
                Set<Slice> slices = Set.copyOf(slicesBuilder.build(
                        sliceGroup,
                        checkInput.packages(),
                        checkInput.analysisPlan().basePackage()));

                var dependencyMaps = problematicDependencies(slices);
                groupResults.add(new SliceGroupResult(
                        sliceGroup.name(),
                        sliceGroup.violationThreshold(),
                        dependencyMaps.illegalDependencies(),
                        dependencyMaps.missingDependencies(),
                        Set.of()));
            } catch (OverlappingSliceDefinitionsException ex) {
                groupResults.add(new SliceGroupResult(
                        sliceGroup.name(),
                        sliceGroup.violationThreshold(),
                        Map.of(),
                        Map.of(),
                        ex.getOverlaps()));
            }
        }

        return new SlicesAnalysisResult(groupResults);
    }

    private DependencyMaps problematicDependencies(Set<Slice> slices) {
        Map<Slice, Set<Slice>> illegalDependenciesBySlice = new HashMap<>();
        Map<Slice, Set<Slice>> missingDependenciesBySlice = new HashMap<>();

        for (Slice slice : slices) {
            Set<Slice> otherSlices = new HashSet<>(slices);
            otherSlices.remove(slice);

            Set<Slice> illegalDependencies = Set.copyOf(slice.findIllegalDependencies(otherSlices));
            Set<Slice> missingDependencies = Set.copyOf(slice.findMissingPredefinedDependencies(otherSlices));

            if (!illegalDependencies.isEmpty()) {
                illegalDependenciesBySlice.put(slice, illegalDependencies);
            }
            if (!missingDependencies.isEmpty()) {
                missingDependenciesBySlice.put(slice, missingDependencies);
            }
        }

        return new DependencyMaps(Map.copyOf(illegalDependenciesBySlice), Map.copyOf(missingDependenciesBySlice));
    }

    private record DependencyMaps(
            Map<Slice, Set<Slice>> illegalDependencies,
            Map<Slice, Set<Slice>> missingDependencies) {
    }
}
