package org.tindalos.guardrails.internal.domain.analyzers.thirdparty;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;
import org.tindalos.guardrails.internal.domain.analyzers.Analyzer;
import org.tindalos.guardrails.internal.domain.constraints.Barrier;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelDefinition;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelGroup;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelId;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Validates third-party dependency usage against configured layer barriers from labels.
 */
public final class ThirdPartyAnalyzer implements Analyzer {

    @Override
    public ThirdPartyViolationsResult analyze(AnalysisInput checkInput) {
        if (!checkInput.thirdPartyExpectations().isPresent()) {
            return new ThirdPartyViolationsResult(Collections.emptyMap(), null);
        }

        var thirdParty = checkInput.thirdPartyExpectations().orElseThrow();
        var barriers = thirdParty.barriers();

        if (barriers.isEmpty()) {
            return new ThirdPartyViolationsResult(Collections.emptyMap(), thirdParty);
        }

        var labelsOpt = checkInput.labels();
        if (labelsOpt.isEmpty()) {
            return new ThirdPartyViolationsResult(Collections.emptyMap(), thirdParty);
        }

        var labelsConstraint = labelsOpt.get();
        var basePackage = checkInput.analysisPlan().basePackage();
        var violations = new HashMap<PackageReference, Set<PackageReference>>();

        for (var aPackage : checkInput.packages()) {
            if (!underBasePackage(aPackage.reference(), basePackage)) {
                continue;
            }

            for (var group : labelsConstraint.labelGroups()) {
                var currentLabelOpt = labelOf(group, aPackage);
                if (currentLabelOpt.isPresent()) {
                    var currentLabel = currentLabelOpt.get();
                    var allowedLibs = getAllowedLibraries(group, currentLabel, barriers);
                    if (allowedLibs != null) {
                        for (var referencedPackage : aPackage.getOwnExternalPackageReferences()) {
                            boolean allowed = allowedLibs.stream()
                                    .anyMatch(referencedPackage::startsWith);
                            if (!allowed) {
                                violations.computeIfAbsent(aPackage.reference(), ignored -> new HashSet<>())
                                        .add(referencedPackage);
                            }
                        }
                    }
                }
            }
        }

        var immutableViolations = new HashMap<PackageReference, Set<PackageReference>>();
        for (var entry : violations.entrySet()) {
            immutableViolations.put(entry.getKey(), Set.copyOf(entry.getValue()));
        }

        return new ThirdPartyViolationsResult(Map.copyOf(immutableViolations), thirdParty);
    }

    @Override
    public boolean isEnabled(Constraints designQualityConstraints) {
        return designQualityConstraints.thirdParty().isPresent();
    }

    private Set<String> getAllowedLibraries(LabelGroup group, LabelId currentLabel, List<Barrier> barriers) {
        var transitiveLabelIds = getTransitiveLabels(group, currentLabel);
        var searchKeys = transitiveLabelIds.stream()
                .map(id -> group.name() + "." + id.value())
                .toList();

        boolean hasBarriersForThisGroup = barriers.stream()
                .anyMatch(b -> b.label().toLowerCase().startsWith(group.name().toLowerCase() + "."));

        if (!hasBarriersForThisGroup) {
            return null;
        }

        return barriers.stream()
                .filter(b -> searchKeys.stream().anyMatch(key -> key.equalsIgnoreCase(b.label())))
                .flatMap(b -> b.components().stream())
                .collect(Collectors.toSet());
    }

    private Set<LabelId> getTransitiveLabels(LabelGroup group, LabelId startLabelId) {
        Set<LabelId> visited = new HashSet<>();
        collectLabels(group, startLabelId, visited);
        return visited;
    }

    private void collectLabels(LabelGroup group, LabelId current, Set<LabelId> visited) {
        if (!visited.add(current)) {
            return;
        }
        var def = group.labels().get(current);
        if (def != null) {
            for (var depId : def.legalDependencies()) {
                collectLabels(group, depId, visited);
            }
        }
    }

    private Optional<LabelId> labelOf(LabelGroup group, PackageWithMetrics aPackage) {
        return group.labels().values().stream()
            .filter(labelDef -> labelDef.packages().stream()
                .anyMatch(pkg -> aPackage.reference().equals(pkg) || aPackage.reference().startsWith(pkg.name() + ".")))
            .map(LabelDefinition::id)
            .findFirst();
    }

    private boolean underBasePackage(PackageReference aPackage, String basePackage) {
        return aPackage.startsWith(basePackage);
    }
}