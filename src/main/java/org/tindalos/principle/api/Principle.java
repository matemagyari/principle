package org.tindalos.principle.api;

import java.util.Objects;
import java.util.Optional;

import org.tindalos.principle.internal.app.PrincipleAnalyser;
import org.tindalos.principle.internal.app.reporters.AnalysisResultsReporter;
import org.tindalos.principle.internal.infrastructure.core.ConstraintsReader;

/**
 * Public entry point for using JPrinciple programmatically.
 * Exposes only minimal operations needed by library clients.
 */
public final class Principle {

    private Principle() {
    }

    @SuppressWarnings("deprecation")
    public static AnalysisPlan readPlan(Optional<String> fileLocation) {
        return new AnalysisPlan(ConstraintsReader.readFromFile(fileLocation));
    }

    @SuppressWarnings("deprecation")
    public static PrincipleAnalyzer analyzer(String rootPackage) {
        Objects.requireNonNull(rootPackage, "rootPackage");

        PrincipleAnalyser internalAnalyzer = org.tindalos.principle.internal.infrastructure.di.Principle
                .createAnalyser(rootPackage);
        AnalysisResultsReporter reporter = org.tindalos.principle.internal.infrastructure.di.Principle
                .createAggregatedYAMLReporter();

        return plan -> {
            var results = internalAnalyzer.analyze(plan.toInternalPlan());
            return new AnalysisOutcome(results.hasViolations(), reporter.summary(results));
        };
    }
}
