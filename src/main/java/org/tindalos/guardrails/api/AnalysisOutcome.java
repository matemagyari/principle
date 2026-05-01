package org.tindalos.guardrails.api;

import java.util.Objects;

/**
 * Minimal public result for a completed analysis run.
 *
 * @param hasViolations true when one or more configured constraints were violated
 * @param summaryYaml YAML summary of the analysis result
 */
public record AnalysisOutcome(boolean hasViolations, String summaryYaml) {

    public AnalysisOutcome {
        summaryYaml = Objects.requireNonNull(summaryYaml, "summaryYaml");
    }
}
