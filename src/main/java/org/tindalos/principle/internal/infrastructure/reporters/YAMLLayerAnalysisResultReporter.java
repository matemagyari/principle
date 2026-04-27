package org.tindalos.principle.internal.infrastructure.reporters;

import org.tindalos.principle.internal.app.reporters.LayerAnalysisResultReporter;
import org.tindalos.principle.internal.domain.analyzers.layering.LayerViolationsResult;

import java.util.stream.Collectors;

/**
 * Reports layering analysis results in YAML format.
 * Produces a structured, machine-readable representation of layer violations
 * suitable for further processing or integration with other tools.
 */
public class YAMLLayerAnalysisResultReporter implements LayerAnalysisResultReporter {

    @Override
    public String report(LayerViolationsResult result) {
        return """
                layer_result:
                  description: Layering constraint
                  violation_count: %s
                  threshold: %s
                  constraint_violated: %s
                %s""".formatted(
                result.violations().size(),
                result.threshold(),
                result.constraintViolated(),
                violationsYaml(result));
    }

    private String violationsYaml(LayerViolationsResult result) {
        if (result.violations().isEmpty()) {
            return "  violations: []\n";
        }

        var violationLines = result.violations().stream()
                .map(v -> "    - referrer: %s\n      referee: %s\n".formatted(v.referrer(), v.referee()))
                .collect(Collectors.joining());

        return "  violations:\n" + violationLines;
    }
}

