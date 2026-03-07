package org.tindalos.principle.infrastructure.reporters;

import org.tindalos.principle.app.reporters.ADPAnalysisResultReporter;
import org.tindalos.principle.domain.analyzers.adp.ADPResult;
import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.packages.PackageReference;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reports ADP analysis results in YAML format.
 * Produces a structured, machine-readable representation of cycle violations
 * suitable for further processing or integration with other tools.
 */
public class YAMLADPAnalysisResultReporter implements ADPAnalysisResultReporter {

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

        var sb = new StringBuilder("  breaking_points:\n");
        result.cyclesByBreakingPoints().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> appendBreakingPoint(sb, entry.getKey(), entry.getValue()));
        return sb.toString();
    }

    private void appendBreakingPoint(StringBuilder sb, PackageReference breakingPoint, Set<Cycle> cycles) {
        sb.append("    - package: ").append(breakingPoint.name()).append("\n");
        sb.append("      cycle_count: ").append(cycles.size()).append("\n");
        sb.append("      cycles:\n");

        cycles.stream()
                .sorted()
                .forEach(cycle -> {
                    List<String> refs = cycle.references().stream()
                            .map(PackageReference::name)
                            .toList();
                    sb.append("        - ").append(refs).append("\n");
                });
    }
}

