package org.tindalos.principle.internal.domain.analyzers.layering;

import java.util.List;

import org.tindalos.principle.internal.domain.analyzers.Analyzer;
import org.tindalos.principle.internal.domain.constraints.Constraints;
import org.tindalos.principle.internal.domain.core.packages.PackageWithMetrics;
import org.tindalos.principle.internal.domain.plan.AnalysisInput;
import org.tindalos.principle.internal.domain.plan.AnalysisPlan;

/**
 * Detects violations of configured architectural layering constraints.
 */
public class LayerViolationAnalyzer implements Analyzer {

    @Override
    public LayerViolationsResult analyze(AnalysisInput checkInput) {
        var layering = checkInput.layeringExpectations().orElseThrow();
        var layerReferences = findViolations(checkInput.packages(), checkInput.analysisPlan());
        return new LayerViolationsResult(layerReferences, layering.violationThreshold());
    }

    @Override
    public boolean isEnabled(Constraints constraints) {
        return constraints.layering().isPresent();
    }

    private List<LayerReference> findViolations(List<PackageWithMetrics> packages, AnalysisPlan configuration) {
        var layers = configuration.constraints().layering()
            .map(layering -> layering.layers().stream()
                .map(layer -> configuration.basePackage() + "." + layer)
                .toList())
            .orElse(List.of());

        return packages.stream()
            .filter(pkg -> pkg.reference().startsWith(configuration.basePackage()))
            .flatMap(pkg -> {
                // Find matching layer for this package
                var matchingLayer = layers.stream()
                    .filter(layer -> pkg.reference().startsWith(layer))
                    .findFirst();
                
                return matchingLayer.stream().flatMap(layer -> {
                    int layerIndex = layers.indexOf(layer);
                    // Return violations for references to lower layers
                    return pkg.getOwnPackageReferences().stream()
                        .filter(ref -> ref.startsWith(configuration.basePackage()))
                        .filter(ref -> layers.stream()
                            .limit(layerIndex)
                            .anyMatch(ref::startsWith))
                        .map(ref -> new LayerReference(pkg.reference().name(), ref.name()));
                });
            })
            .toList();
    }
}