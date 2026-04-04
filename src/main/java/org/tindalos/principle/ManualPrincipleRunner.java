package org.tindalos.principle;

import java.util.Optional;

import org.tindalos.principle.infrastructure.ConsolePrinter;
import org.tindalos.principle.infrastructure.ConstraintsReader;
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
        PoorMansDIContainer.buildAnalyzer(plan.basePackage(), printer).analyze(plan);
    }
}
