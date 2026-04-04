package org.tindalos.principle.app;

import java.util.List;

import org.tindalos.principle.domain.AnalysisInput;
import org.tindalos.principle.domain.AnalysisRunner;
import org.tindalos.principle.domain.core.AnalysisPlan;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter;
import org.tindalos.principle.infrastructure.PackageListBuilder;

/**
 * Main application entry point for running architectural analysis.
 *
 * <p>All dependencies are injected through the constructor to keep wiring explicit.</p>
 */
public class PrincipleApplication {

    private final AnalysisPlanValidator inputValidator;
    private final PackageListBuilder packageListBuilder;
    private final NodeBuilder nodeBuilder;
    private final AnalysisRunner analysisRunner;
    private final AnalysisResultsReporter analysisResultsReporter;
    private final Printer printer;

    public PrincipleApplication(
            AnalysisPlanValidator inputValidator,
            PackageListBuilder packageListBuilder,
            NodeBuilder nodeBuilder,
            AnalysisRunner analysisRunner,
            AnalysisResultsReporter analysisResultsReporter,
            Printer printer) {
        this.inputValidator = inputValidator;
        this.packageListBuilder = packageListBuilder;
        this.nodeBuilder = nodeBuilder;
        this.analysisRunner = analysisRunner;
        this.analysisResultsReporter = analysisResultsReporter;
        this.printer = printer;
    }

    public ValidationResult analyze(AnalysisPlan analysisPlan) {
        ValidationResult validationResult = inputValidator.validate(analysisPlan);

        if (!validationResult.success()) {
            return validationResult;
        }

        List<PackageWithMetrics> packages = packageListBuilder.build().stream()
                .map(pkg -> (PackageWithMetrics) pkg)
                .toList();
        var nodes = nodeBuilder.build(analysisPlan.basePackage());

        var analysisResults = analysisRunner.run(new AnalysisInput(packages, nodes, analysisPlan));

        printer.printInfo(analysisResultsReporter.summary(analysisResults));

        boolean success = analysisResults.stream().noneMatch(result -> result.constraintViolated());
        return new ValidationResult(success, success ? "" : "Expectations failed");
    }
}
