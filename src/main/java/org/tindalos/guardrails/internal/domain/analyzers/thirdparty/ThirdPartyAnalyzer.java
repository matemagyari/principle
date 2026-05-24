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
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceDefinition;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceGroup;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceId;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Validates third-party dependency usage against configured layer barriers from slices.
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

        var slicesOpt = checkInput.slices();
        if (slicesOpt.isEmpty()) {
            return new ThirdPartyViolationsResult(Collections.emptyMap(), thirdParty);
        }

        var slicesConstraint = slicesOpt.get();
        var basePackage = checkInput.analysisPlan().basePackage();
        var violations = new HashMap<PackageReference, Set<PackageReference>>();

        for (var aPackage : checkInput.packages()) {
            if (!underBasePackage(aPackage.reference(), basePackage)) {
                continue;
            }

            for (var group : slicesConstraint.sliceGroups()) {
                var currentSliceOpt = sliceOf(group, aPackage);
                if (currentSliceOpt.isPresent()) {
                    var currentSlice = currentSliceOpt.get();
                    var allowedLibs = getAllowedLibraries(group, currentSlice, barriers);
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

    private Set<String> getAllowedLibraries(SliceGroup group, SliceId currentSlice, List<Barrier> barriers) {
        var transitiveSliceIds = getTransitiveSlices(group, currentSlice);
        var searchKeys = transitiveSliceIds.stream()
                .map(id -> group.name() + "." + id.value())
                .toList();

        boolean hasBarriersForThisGroup = barriers.stream()
                .anyMatch(b -> b.slice().toLowerCase().startsWith(group.name().toLowerCase() + "."));

        if (!hasBarriersForThisGroup) {
            return null;
        }

        return barriers.stream()
                .filter(b -> searchKeys.stream().anyMatch(key -> key.equalsIgnoreCase(b.slice())))
                .flatMap(b -> b.components().stream())
                .collect(Collectors.toSet());
    }

    private Set<SliceId> getTransitiveSlices(SliceGroup group, SliceId startSliceId) {
        Set<SliceId> visited = new HashSet<>();
        collectSlices(group, startSliceId, visited);
        return visited;
    }

    private void collectSlices(SliceGroup group, SliceId current, Set<SliceId> visited) {
        if (!visited.add(current)) {
            return;
        }
        var def = group.slices().get(current);
        if (def != null) {
            for (var depId : def.legalDependencies()) {
                collectSlices(group, depId, visited);
            }
        }
    }

    private Optional<SliceId> sliceOf(SliceGroup group, PackageWithMetrics aPackage) {
        return group.slices().values().stream()
            .filter(sliceDef -> sliceDef.packages().stream()
                .anyMatch(pkg -> aPackage.reference().equals(pkg) || aPackage.reference().startsWith(pkg.name() + ".")))
            .map(SliceDefinition::id)
            .findFirst();
    }

    private boolean underBasePackage(PackageReference aPackage, String basePackage) {
        return aPackage.startsWith(basePackage);
    }
}