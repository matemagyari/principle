package org.tindalos.principle.api;

import java.util.Objects;

import org.tindalos.principle.api.spi.AnalysisReporter;

/**
 * Public analyzer contract for running architecture analysis.
 */
@FunctionalInterface
public interface PrincipleAnalyzer {

    AnalysisOutcome analyze(AnalysisPlan plan);

    default AnalysisOutcome analyze(AnalysisPlan plan, AnalysisReporter reporter) {
        Objects.requireNonNull(reporter, "reporter");
        var outcome = analyze(plan);
        reporter.report(outcome);
        return outcome;
    }
}
