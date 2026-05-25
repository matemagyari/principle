package org.tindalos.guardrails.internal.domain.analyzers.labels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.guardrails.internal.domain.analyzers.Analyzer;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.labels.OverlappingLabelDefinitionsException;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelGroup;
import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;

/**
 * Analyzes discovered package structures against arbitrary label definitions and boundaries.
 */
public class LabelsAnalyzer implements Analyzer {

    private final LabelsBuilder labelsBuilder;

    public LabelsAnalyzer(LabelsBuilder labelsBuilder) {
        this.labelsBuilder = labelsBuilder;
    }

    @Override
    public boolean isEnabled(Constraints designQualityConstraints) {
        return designQualityConstraints.labels().isPresent();
    }

    @Override
    public LabelsAnalysisResult analyze(AnalysisInput checkInput) {
        if (checkInput.labels().isEmpty()) {
            return new LabelsAnalysisResult(List.of());
        }

        var labelsConstraint = checkInput.labels().orElseThrow();
        List<LabelGroupResult> groupResults = new ArrayList<>();

        for (LabelGroup labelGroup : labelsConstraint.labelGroups()) {
            try {
                Set<Label> labels = Set.copyOf(labelsBuilder.build(
                        labelGroup,
                        checkInput.packages(),
                        checkInput.analysisPlan().basePackage()));

                var dependencyMaps = problematicDependencies(labels);
                groupResults.add(new LabelGroupResult(
                        labelGroup.name(),
                        labelGroup.violationThreshold(),
                        dependencyMaps.illegalDependencies(),
                        dependencyMaps.missingDependencies(),
                        Set.of()));
            } catch (OverlappingLabelDefinitionsException ex) {
                groupResults.add(new LabelGroupResult(
                        labelGroup.name(),
                        labelGroup.violationThreshold(),
                        Map.of(),
                        Map.of(),
                        ex.getOverlaps()));
            }
        }

        return new LabelsAnalysisResult(groupResults);
    }

    private DependencyMaps problematicDependencies(Set<Label> labels) {
        Map<Label, Set<Label>> illegalDependenciesByLabel = new HashMap<>();
        Map<Label, Set<Label>> missingDependenciesByLabel = new HashMap<>();

        for (Label label : labels) {
            Set<Label> otherLabels = new HashSet<>(labels);
            otherLabels.remove(label);

            Set<Label> illegalDependencies = Set.copyOf(label.findIllegalDependencies(otherLabels));
            Set<Label> missingDependencies = Set.copyOf(label.findMissingPredefinedDependencies(otherLabels));

            if (!illegalDependencies.isEmpty()) {
                illegalDependenciesByLabel.put(label, illegalDependencies);
            }
            if (!missingDependencies.isEmpty()) {
                missingDependenciesByLabel.put(label, missingDependencies);
            }
        }

        return new DependencyMaps(Map.copyOf(illegalDependenciesByLabel), Map.copyOf(missingDependenciesByLabel));
    }

    private record DependencyMaps(
            Map<Label, Set<Label>> illegalDependencies,
            Map<Label, Set<Label>> missingDependencies) {
    }
}
