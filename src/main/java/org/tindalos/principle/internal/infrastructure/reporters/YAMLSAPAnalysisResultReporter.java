package org.tindalos.principle.internal.infrastructure.reporters;

import org.tindalos.principle.internal.app.reporters.SAPAnalysisResultReporter;
import org.tindalos.principle.internal.domain.analyzers.sap.SAPResult;
import org.tindalos.principle.internal.domain.core.packages.PackageWithMetrics;

import java.util.stream.Collectors;

/**
 * Reports Stable Abstractions Principle (SAP) analysis results in YAML format.
 * Produces a structured, machine-readable representation of packages that exceed
 * the allowed distance from the main sequence.
 */
public class YAMLSAPAnalysisResultReporter implements SAPAnalysisResultReporter {

    @Override
    public String report(SAPResult result) {
        return """
                sap_result:
                  description: Stable Abstractions Principle constraint
                  violation_count: %s
                  threshold: %s
                  max_distance: %s
                  constraint_violated: %s
                %s""".formatted(
                result.outlierPackages().size(),
                result.threshold(),
                result.sapExpectation().maxDistance(),
                result.constraintViolated(),
                violationsYaml(result));
    }

    private String violationsYaml(SAPResult result) {
        if (result.outlierPackages().isEmpty()) {
            return "  violations: []\n";
        }

        var violationLines = result.outlierPackages().stream()
                .map(pkg -> "    - package: %s\n      distance: %s\n"
                        .formatted(pkg.reference().name(), pkg.getMetrics().distance()))
                .collect(Collectors.joining());

        return "  violations:\n" + violationLines;
    }
}

