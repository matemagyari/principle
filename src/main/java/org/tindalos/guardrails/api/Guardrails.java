package org.tindalos.guardrails.api;

import java.util.Objects;
import java.util.Optional;

import org.tindalos.guardrails.internal.app.GuardrailsAnalyser;
import org.tindalos.guardrails.internal.app.reporters.AnalysisResultsReporter;
import org.tindalos.guardrails.internal.infrastructure.constraints.ConstraintsReader;

/**
 * Public entry point for using Guardrails programmatically.
 * Exposes only minimal operations needed by library clients.
 */
public final class Guardrails {

    private Guardrails() {
    }

    @SuppressWarnings("deprecation")
    public static AnalysisPlan readPlan(Optional<String> fileLocation) {
        return new AnalysisPlan(ConstraintsReader.readFromFile(fileLocation));
    }

    @SuppressWarnings("deprecation")
    public static GuardrailsAnalyzer analyzer(String rootPackage) {
        Objects.requireNonNull(rootPackage, "rootPackage");

        GuardrailsAnalyser internalAnalyzer = org.tindalos.guardrails.internal.infrastructure.di.Guardrails
                .createAnalyser(rootPackage);
        AnalysisResultsReporter reporter = org.tindalos.guardrails.internal.infrastructure.di.Guardrails
                .createAggregatedYAMLReporter();

        return plan -> {
            var results = internalAnalyzer.analyze(plan.toInternalPlan());
            return new AnalysisOutcome(results.hasViolations(), reporter.summary(results));
        };
    }
}
