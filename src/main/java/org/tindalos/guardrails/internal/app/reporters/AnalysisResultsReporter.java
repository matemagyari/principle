package org.tindalos.guardrails.internal.app.reporters;

import org.tindalos.guardrails.internal.domain.AggregatedAnalysisResults;
import org.tindalos.guardrails.internal.domain.core.AnalysisResult;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Aggregates individual analysis reports into a single YAML summary.
 */
public final class AnalysisResultsReporter {

    private final java.util.List<AnalysisResultReporter<?>> reporters = new ArrayList<>();

    public AnalysisResultsReporter(java.util.List<AnalysisResultReporter<?>> reporters) {
        this.reporters.addAll(reporters);
    }

    public String summary(AggregatedAnalysisResults results) {
        var reports = results.results().stream()
            .map(result -> new ReportWithViolation(
                    getReporter(result).report(result),
                    result.constraintViolated()))
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
        return reporters.stream()
                .filter(reporter -> reporter.supports(t))
                .findFirst()
                .map(reporter -> {
                    return (AnalysisResultReporter<T>) reporter;
                })
                .get();
    }

    private record ReportWithViolation(String report, boolean violated) {
    }
}
