package org.tindalos.principle.internal.domain.analyzers.thirdparty;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.tindalos.principle.internal.domain.plan.AnalysisInput;
import org.tindalos.principle.internal.domain.analyzers.Analyzer;
import org.tindalos.principle.internal.domain.constraints.Barrier;
import org.tindalos.principle.internal.domain.constraints.Constraints;
import org.tindalos.principle.internal.domain.core.packages.PackageReference;
import org.tindalos.principle.internal.domain.core.packages.PackageWithMetrics;

/**
 * Validates third-party dependency usage against configured layer barriers.
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

        var layers = checkInput.layeringExpectations().orElseThrow().layers();
        var basePackage = checkInput.analysisPlan().basePackage();
        var violations = new HashMap<PackageReference, Set<PackageReference>>();

        for (var aPackage : checkInput.packages()) {
            if (!underBasePackage(aPackage.reference(), basePackage)) {
                continue;
            }

            layerOf(layers, basePackage, aPackage)
                .ifPresent(layer -> {
                    for (var referencedPackage : aPackage.getOwnExternalPackageReferences()) {
                        if (outOfAllowedComponents(layer, layers, barriers, referencedPackage)) {
                            violations.computeIfAbsent(aPackage.reference(), ignored -> new HashSet<>())
                                    .add(referencedPackage);
                        }
                    }
                });
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

    private List<String> allowedComponentsForLayer(List<String> layers, String layer, List<Barrier> barriers) {
        var index = layers.indexOf(layer);
        if (index < 0) {
            return List.of();
        }

        var innerLayers = layers.subList(index, layers.size());
        return barriers.stream()
                .filter(barrier -> innerLayers.contains(barrier.layer()))
                .flatMap(barrier -> barrier.components().stream())
                .toList();
    }

    private boolean outOfAllowedComponents(String layer, List<String> layers, List<Barrier> barriers, PackageReference referencedPackage) {
        return allowedComponentsForLayer(layers, layer, barriers).stream()
                .noneMatch(referencedPackage::startsWith);
    }

    private Optional<String> layerOf(List<String> layers, String basePackage, PackageWithMetrics aPackage) {
        return layers.stream()
            .filter(layer -> aPackage.reference().startsWith(basePackage + "." + layer))
            .findFirst();
    }

    private boolean underBasePackage(PackageReference aPackage, String basePackage) {
        return aPackage.startsWith(basePackage);
    }
}