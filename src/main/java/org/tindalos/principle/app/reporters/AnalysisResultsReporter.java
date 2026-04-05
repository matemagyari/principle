package org.tindalos.principle.app.reporters;

import java.util.Arrays;

import org.tindalos.principle.domain.AggregatedAnalysisResults;
import org.tindalos.principle.domain.core.AnalysisResult;
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesResult;
import org.tindalos.principle.domain.analyzers.adp.ADPResult;
import org.tindalos.principle.domain.analyzers.layering.LayerViolationsResult;
import org.tindalos.principle.domain.analyzers.sap.SAPResult;
import org.tindalos.principle.domain.analyzers.sdp.SDPResult;
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult;
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyViolationsResult;

/**
 * Aggregates individual analysis reports into a single YAML summary.
 */
public final class AnalysisResultsReporter {

    private final ADPAnalysisResultReporter adpReporter;
    private final LayerAnalysisResultReporter layerReporter;
    private final ThirdPartyAnalysisResultReporter thirdPartyReporter;
    private final SAPAnalysisResultReporter sapReporter;
    private final ComponentDependencyAnalysisResultReporter componentDependencyReporter;
    private final SubmodulesBlueprintAnalysisResultReporter submodulesBlueprintReporter;
    private final SDPAnalysisResultReporter sdpReporter;
    private final PackageCohesionAnalysisResultReporter cohesionReporter;

    public AnalysisResultsReporter(
            ADPAnalysisResultReporter adpReporter,
            LayerAnalysisResultReporter layerReporter,
            ThirdPartyAnalysisResultReporter thirdPartyReporter,
            SAPAnalysisResultReporter sapReporter,
            ComponentDependencyAnalysisResultReporter componentDependencyReporter,
            SubmodulesBlueprintAnalysisResultReporter submodulesBlueprintReporter,
            SDPAnalysisResultReporter sdpReporter,
            PackageCohesionAnalysisResultReporter cohesionReporter) {
        this.adpReporter = adpReporter;
        this.layerReporter = layerReporter;
        this.thirdPartyReporter = thirdPartyReporter;
        this.sapReporter = sapReporter;
        this.componentDependencyReporter = componentDependencyReporter;
        this.submodulesBlueprintReporter = submodulesBlueprintReporter;
        this.sdpReporter = sdpReporter;
        this.cohesionReporter = cohesionReporter;
    }

    public String summary(AggregatedAnalysisResults results) {
        var reports = results.results().stream()
            .map(this::toReport)
            .toList();

        var success = !results.hasViolations();
        var violatedNames = reports.stream()
                .filter(ReportWithViolation::violated)
                .map(ReportWithViolation::report)
                .map(report -> {
                    var separator = report.indexOf(':');
                    return separator >= 0 ? report.substring(0, separator) : report;
                })
                .toList();

        var description = success
                ? "All constraints satisfied"
                : "Constraints violated in: " + String.join(", ", violatedNames);

        var resultsYaml = reports.isEmpty()
            ? "  results: {}\n"
            : "  results:\n" + reports.stream()
                .map(ReportWithViolation::report)
                .map(this::indentYaml)
                .collect(java.util.stream.Collectors.joining());

        return """
                analysis_summary:
                  success: %s
                  description: "%s"
                %s""".formatted(success, description, resultsYaml);
    }

    private String indentYaml(String yaml) {
        var lines = yaml.split("\\n", -1);
        var limit = lines.length;
        if (limit > 0 && lines[limit - 1].isEmpty()) {
            limit -= 1;
        }
        return Arrays.stream(lines, 0, limit)
            .map(line -> "    " + line + "\n")
            .collect(java.util.stream.Collectors.joining());
    }

    private ReportWithViolation toReport(AnalysisResult result) {
        return switch (result) {
            case ADPResult typed -> new ReportWithViolation(adpReporter.report(typed), result.constraintViolated());
            case LayerViolationsResult typed -> new ReportWithViolation(layerReporter.report(typed), result.constraintViolated());
            case ThirdPartyViolationsResult typed -> new ReportWithViolation(thirdPartyReporter.report(typed), result.constraintViolated());
            case SDPResult typed -> new ReportWithViolation(sdpReporter.report(typed), result.constraintViolated());
            case SAPResult typed -> new ReportWithViolation(sapReporter.report(typed), result.constraintViolated());
            case ComponentDependenciesResult typed -> new ReportWithViolation(componentDependencyReporter.report(typed), result.constraintViolated());
            case SubmodulesBlueprintAnalysisResult typed -> new ReportWithViolation(submodulesBlueprintReporter.report(typed), result.constraintViolated());
            case CohesionAnalysisResult typed -> new ReportWithViolation(cohesionReporter.report(typed), result.constraintViolated());
            default -> throw new RuntimeException("terrible thing - no result type");
        };
    }

    private record ReportWithViolation(String report, boolean violated) {
    }
}
