package org.tindalos.principle.infrastructure.plugin;

import java.util.Optional;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.tindalos.principle.domain.constraints.exception.InvalidConfigurationException;
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter;
import org.tindalos.principle.infrastructure.ConstraintsReader;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager;
import org.tindalos.principle.utils.logging.SimpleLogger;
import org.tindalos.principle.utils.logging.TheLogger;

@Mojo(name = "check")
public class DesignQualityCheckerMojo extends AbstractMojo {

    @Parameter(property = "check.location")
    private String location;

    @Override
    public void execute() throws MojoFailureException {
        TheLogger.setLogger(new SimpleLogger() {
            @Override
            public void info(String msg) {
                getLog().info(msg);
            }

            @Override
            public void error(String msg) {
                getLog().info(msg);
            }
        });

        ReportsDirectoryManager.ensureReportsDirectoryExists();

        var plan = ConstraintsReader.readFromFile(Optional.ofNullable(location));

        var printer = new LogPrinter(getLog());
        var analyzer = PoorMansDIContainer.buildAnalyzer(plan.basePackage());
        var reporter = PoorMansDIContainer.createReporter();
        try {
            var result = analyzer.analyze(plan);
            printer.printInfo(reporter.summary(result));
            if (result.hasViolations()) {
                throw new MojoFailureException("\nNumber of violations exceeds allowed limits!");
            }
        } catch (InvalidConfigurationException ex) {
            throw new MojoFailureException(ex.getMessage());
        } catch (RuntimeException ex) {
            throw new MojoFailureException("Unexpected error", ex);
        }
    }
}
