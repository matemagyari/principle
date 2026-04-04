package org.tindalos.principle.domain.analyzers.layering;

import java.util.ArrayList;
import java.util.List;

import org.tindalos.principle.domain.plan.AnalysisInput;
import org.tindalos.principle.domain.analyzers.Analyzer;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.plan.AnalysisPlan;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;

/**
 * Detects violations of configured architectural layering constraints.
 */
public class LayerViolationAnalyzer implements Analyzer {

    @Override
    public LayerViolationsResult analyze(AnalysisInput checkInput) {
        var layering = checkInput.layeringExpectations().get();
        var layerReferences = findViolations(checkInput.packages(), checkInput.analysisPlan());
        return new LayerViolationsResult(layerReferences, layering.violationThreshold());
    }

    @Override
    public boolean isEnabled(Constraints expectations) {
        return expectations.layering().isPresent();
    }

    private List<LayerReference> findViolations(List<PackageWithMetrics> packages, AnalysisPlan configuration) {
        var layers = configuration.constraints().layering().get().layers().stream()
                .map(layer -> configuration.basePackage() + "." + layer)
                .toList();

        var violations = new ArrayList<LayerReference>();
        for (var aPackage : packages) {
            if (!aPackage.reference().startsWith(configuration.basePackage())) {
                continue;
            }

            String layer = null;
            for (var candidate : layers) {
                if (aPackage.reference().startsWith(candidate)) {
                    layer = candidate;
                    break;
                }
            }
            if (layer == null) {
                continue;
            }

            var layerIndex = layers.indexOf(layer);
            for (PackageReference referencedPackage : aPackage.getOwnPackageReferences()) {
                if (!referencedPackage.startsWith(configuration.basePackage())) {
                    continue;
                }

                for (int i = 0; i < layerIndex; i++) {
                    var referencedLayer = layers.get(i);
                    if (referencedPackage.startsWith(referencedLayer)) {
                        violations.add(new LayerReference(aPackage.reference().name(), referencedPackage.name()));
                    }
                }
            }
        }

        return violations;
    }
}