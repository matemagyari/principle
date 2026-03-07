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
        var sb = new StringBuilder();
        sb.append("adp_result:\n");
        sb.append("  description: Acyclic Package Dependency Principle constraint\n");
        sb.append("  violation_count: ").append(result.cyclesByBreakingPoints().size()).append("\n");
        sb.append("  threshold: ").append(result.threshold()).append("\n");
        sb.append("  constraint_violated: ").append(result.constraintViolated()).append("\n");

        if (result.cyclesByBreakingPoints().isEmpty()) {
            sb.append("  breaking_points: []\n");
        } else {
            sb.append("  breaking_points:\n");

            result.cyclesByBreakingPoints().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> appendBreakingPoint(sb, entry.getKey(), entry.getValue()));
        }

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

