package org.tindalos.guardrails.internal.app.reporters;

import java.util.Arrays;

import org.tindalos.guardrails.internal.domain.AggregatedAnalysisResults;
import org.tindalos.guardrails.internal.domain.analyzers.acd.ComponentDependenciesResult;
import org.tindalos.guardrails.internal.domain.analyzers.adp.ADPResult;
import org.tindalos.guardrails.internal.domain.analyzers.layering.LayerViolationsResult;
import org.tindalos.guardrails.internal.domain.analyzers.sap.SAPResult;
import org.tindalos.guardrails.internal.domain.analyzers.sdp.SDPResult;
import org.tindalos.guardrails.internal.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.guardrails.internal.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult;
import org.tindalos.guardrails.internal.domain.analyzers.thirdparty.ThirdPartyViolationsResult;
import org.tindalos.guardrails.internal.domain.core.AnalysisResult;

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

    private final java.util.List<AnalysisResultReporter<?>> reporters;

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

        this.reporters = Arrays.asList(
                adpReporter,
                layerReporter,
                thirdPartyReporter,
                sapReporter,
                componentDependencyReporter,
                submodulesBlueprintReporter,
                sdpReporter,
                cohesionReporter
        );
    }

    public String summary(AggregatedAnalysisResults results) {
        var reports = results.results().stream()
            .map(result -> new ReportWithViolation(getReporter(result).report(result), result.constraintViolated()))
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

    private <T extends AnalysisResult> AnalysisResultReporter<T> getReporter(T t) {
        java.util.List<AnalysisResultReporter<?>> reporters = Arrays.asList(
                adpReporter,
                layerReporter,
                thirdPartyReporter,
                sapReporter,
                componentDependencyReporter,
                submodulesBlueprintReporter,
                sdpReporter,
                cohesionReporter
        );

        return reporters.stream()
                .filter(reporter -> reporter.resultType().isInstance(t))
                .findFirst()
                .map(reporter -> {
                    return (AnalysisResultReporter<T>) reporter;
                })
                .get();
    }

    private record ReportWithViolation(String report, boolean violated) {
    }
}
