package org.tindalos.principle.internal.infrastructure.reporters;

import org.tindalos.principle.internal.app.reporters.SDPAnalysisResultReporter;
import org.tindalos.principle.internal.domain.analyzers.sdp.SDPResult;
import org.tindalos.principle.internal.domain.analyzers.sdp.SDPViolation;

import java.util.stream.Collectors;

/**
 * Reports Stable Dependencies Principle (SDP) analysis results in YAML format.
 * Produces a structured, machine-readable representation of violations where
 * a package depends on another package with higher instability.
 */
public class YAMLSDPAnalysisResultReporter implements SDPAnalysisResultReporter {

    @Override
    public String report(SDPResult result) {
        return """
                sdp_result:
                  description: Stable Dependencies Principle constraint
                  violation_count: %s
                  threshold: %s
                  constraint_violated: %s
                %s""".formatted(
                result.violations().size(),
                result.threshold(),
                result.constraintViolated(),
                violationsYaml(result));
    }

    private String violationsYaml(SDPResult result) {
        if (result.violations().isEmpty()) {
            return "  violations: []\n";
        }

        var violationLines = result.violations().stream()
                .map(this::violationYaml)
                .collect(Collectors.joining());

        return "  violations:\n" + violationLines;
    }

    private String violationYaml(SDPViolation violation) {
        return """
                    - depender: %s
                      depender_instability: %s
                      dependee: %s
                      dependee_instability: %s
                """.formatted(
                violation.depender().reference().name(),
                violation.depender().getMetrics().instability(),
                violation.dependee().reference().name(),
                violation.dependee().getMetrics().instability());
    }
}

