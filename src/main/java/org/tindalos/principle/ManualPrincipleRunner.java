package org.tindalos.principle;

import java.util.Optional;

import org.tindalos.principle.domain.AggregatedAnalysisResults;
import org.tindalos.principle.app.reporters.AnalysisResultsReporter;
import org.tindalos.principle.infrastructure.ConsolePrinter;
import org.tindalos.principle.infrastructure.core.ConstraintsReader;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.utils.logging.SimpleLogger;
import org.tindalos.principle.utils.logging.TheLogger;

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
        AnalysisResultsReporter reporter = PoorMansDIContainer.createReporter();
        AggregatedAnalysisResults results = PoorMansDIContainer.buildAnalyzer(plan.basePackage()).analyze(plan);
        printer.printInfo(reporter.summary(results));
    }
}
