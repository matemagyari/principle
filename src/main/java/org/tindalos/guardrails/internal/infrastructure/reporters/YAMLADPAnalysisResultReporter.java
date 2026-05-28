package org.tindalos.guardrails.internal.infrastructure.reporters;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.app.reporters.ADPAnalysisResultReporter;
import org.tindalos.guardrails.internal.domain.analyzers.adp.ADPResult;
import org.tindalos.guardrails.internal.domain.analyzers.adp.Cycle;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;

/**
 * Reports ADP analysis results in YAML format.
 * Produces a structured, machine-readable representation of cycle violations
 * suitable for further processing or integration with other tools.
 */
public class YAMLADPAnalysisResultReporter implements ADPAnalysisResultReporter {

    @Override
    public Class<ADPResult> resultType() {
        return ADPResult.class;
    }

    @Override
    public String report(ADPResult result) {
        return """
                adp_result:
                  description: Acyclic Package Dependency Principle constraint
                  violation_count: %s
                  threshold: %s
                  constraint_violated: %s
                %s""".formatted(
                result.cyclesByBreakingPoints().size(),
                result.threshold(),
                result.constraintViolated(),
                breakingPointsYaml(result));
    }

    private String breakingPointsYaml(ADPResult result) {
        if (result.cyclesByBreakingPoints().isEmpty()) {
            return "  breaking_points: []\n";
        }

        var entries = result.cyclesByBreakingPoints().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> breakingPointYaml(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining());

        return "  breaking_points:\n" + entries;
    }

    private String breakingPointYaml(PackageReference breakingPoint, Set<Cycle> cycles) {
        var cycleLines = cycles.stream()
                .sorted()
                .map(cycle -> "        - %s\n".formatted(
                        cycle.references().stream().map(PackageReference::name).toList()))
                .collect(Collectors.joining());

        return """
                    - package: %s
                      cycle_count: %s
                      cycles:
                %s""".formatted(breakingPoint.name(), cycles.size(), cycleLines);
    }
}

