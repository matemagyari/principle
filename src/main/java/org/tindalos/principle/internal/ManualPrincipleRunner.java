package org.tindalos.principle.internal;

import java.util.Optional;

import org.tindalos.principle.internal.app.reporters.AnalysisResultsReporter;
import org.tindalos.principle.internal.domain.AggregatedAnalysisResults;
import org.tindalos.principle.internal.infrastructure.ConsolePrinter;
import org.tindalos.principle.internal.infrastructure.core.ConstraintsReader;
import org.tindalos.principle.internal.infrastructure.di.Principle;
import org.tindalos.principle.internal.utils.logging.SimpleLogger;
import org.tindalos.principle.internal.utils.logging.TheLogger;

public final class ManualPrincipleRunner {

    private ManualPrincipleRunner() {
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
                Optional.of("/Users/mate.magyari/private/PrivateProjects/principle/principle.yml"));
        AnalysisResultsReporter reporter = Principle.createAggregatedYAMLReporter();
        AggregatedAnalysisResults results = Principle.createAnalyser(plan.basePackage()).analyze(plan);
        printer.printInfo(reporter.summary(results));
    }
}
