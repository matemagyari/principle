package org.tindalos.principle.domain.resultprocessing.reporter;

import java.util.ArrayList;

import org.tindalos.principle.app.reporters.ADPAnalysisResultReporter;
import org.tindalos.principle.app.reporters.ComponentDependencyAnalysisResultReporter;
import org.tindalos.principle.app.reporters.LayerAnalysisResultReporter;
import org.tindalos.principle.app.reporters.SAPAnalysisResultReporter;
import org.tindalos.principle.app.reporters.SDPAnalysisResultReporter;
import org.tindalos.principle.app.reporters.SubmodulesBlueprintAnalysisResultReporter;
import org.tindalos.principle.app.reporters.ThirdPartyAnalysisResultReporter;
import org.tindalos.principle.domain.AnalysisResult;
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesResult;
import org.tindalos.principle.domain.analyzers.adp.ADPResult;
import org.tindalos.principle.domain.analyzers.layering.LayerViolationsResult;
import org.tindalos.principle.domain.analyzers.sap.SAPResult;
import org.tindalos.principle.domain.analyzers.sdp.SDPResult;
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult;
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyViolationsResult;
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionAnalysisResultReporter;

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

    public String summary(scala.collection.immutable.List<AnalysisResult> results) {
        var reports = new ArrayList<ReportWithViolation>();
        var iterator = results.iterator();
        while (iterator.hasNext()) {
            reports.add(toReport(iterator.next()));
        }

        var success = reports.stream().noneMatch(ReportWithViolation::violated);
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

        String resultsYaml;
        if (reports.isEmpty()) {
            resultsYaml = "  results: {}\n";
        } else {
            var builder = new StringBuilder("  results:\n");
            for (var report : reports) {
                builder.append(indentYaml(report.report()));
            }
            resultsYaml = builder.toString();
        }

        return """
                analysis_summary:
                  success: %s
                  description: "%s"
                %s""".formatted(success, description, resultsYaml);
    }

    private String indentYaml(String yaml) {
        var lines = yaml.split("\\n", -1);
        var length = lines.length;
        if (length > 0 && lines[length - 1].isEmpty()) {
            length -= 1;
        }

        var builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append("    ").append(lines[i]).append("\n");
        }
        return builder.toString();
    }

    private ReportWithViolation toReport(AnalysisResult result) {
        String report = switch (result) {
            case ADPResult typed -> adpReporter.report(typed);
            case LayerViolationsResult typed -> layerReporter.report(typed);
            case ThirdPartyViolationsResult typed -> thirdPartyReporter.report(typed);
            case SDPResult typed -> sdpReporter.report(typed);
            case SAPResult typed -> sapReporter.report(typed);
            case ComponentDependenciesResult typed -> componentDependencyReporter.report(typed);
            case SubmodulesBlueprintAnalysisResult typed -> submodulesBlueprintReporter.report(typed);
            case CohesionAnalysisResult typed -> cohesionReporter.report(typed);
            default -> throw new RuntimeException("terrible thing - no result type");
        };
        return new ReportWithViolation(report, result.constraintViolated());
    }

    private record ReportWithViolation(String report, boolean violated) {
    }
}
