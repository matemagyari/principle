package org.tindalos.principle.api.spi;

import java.util.Objects;

import org.tindalos.principle.api.AnalysisOutcome;

/**
 * SPI for publishing or persisting analysis outcomes.
 */
@FunctionalInterface
public interface AnalysisReporter {

    void report(AnalysisOutcome outcome);

    static AnalysisReporter noOp() {
        return outcome -> Objects.requireNonNull(outcome, "outcome");
    }
}
