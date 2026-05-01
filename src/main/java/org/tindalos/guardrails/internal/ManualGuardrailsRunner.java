package org.tindalos.guardrails.internal;

import java.util.Optional;

import org.tindalos.guardrails.internal.app.reporters.AnalysisResultsReporter;
import org.tindalos.guardrails.internal.domain.AggregatedAnalysisResults;
import org.tindalos.guardrails.internal.infrastructure.ConsolePrinter;
import org.tindalos.guardrails.internal.infrastructure.constraints.ConstraintsReader;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;
import org.tindalos.guardrails.internal.utils.logging.SimpleLogger;
import org.tindalos.guardrails.internal.utils.logging.TheLogger;

public final class ManualGuardrailsRunner {

    private ManualGuardrailsRunner() {
    }

    public static void main(String[] args) {
        var printer = new ConsolePrinter();

        TheLogger.setLogger(new SimpleLogger() {
            @Override
            public void info(String msg) {
                printer.printInfo(msg);
            }

            @Override
            public void error(String msg) {
                printer.printWarning(msg);
            }
        });

        var plan = ConstraintsReader.readFromFile(
                Optional.of("/Users/mate.magyari/private/PrivateProjects/guardrails/guardrails.yml"));
        AnalysisResultsReporter reporter = Guardrails.createAggregatedYAMLReporter();
        AggregatedAnalysisResults results = Guardrails.createAnalyser(plan.basePackage()).analyze(plan);
        printer.printInfo(reporter.summary(results));
    }
}
