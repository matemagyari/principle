package org.tindalos.principle.app;

import java.util.List;

import org.tindalos.principle.domain.AggregatedAnalysisResults;
import org.tindalos.principle.domain.AnalysisInput;
import org.tindalos.principle.domain.AnalysisRunner;
import org.tindalos.principle.domain.constraints.exception.InvalidConfigurationException;
import org.tindalos.principle.domain.core.AnalysisPlan;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;
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

    public PrincipleApplication(
            AnalysisPlanValidator inputValidator,
            PackageListBuilder packageListBuilder,
            NodeBuilder nodeBuilder,
            AnalysisRunner analysisRunner) {
        this.inputValidator = inputValidator;
        this.packageListBuilder = packageListBuilder;
        this.nodeBuilder = nodeBuilder;
        this.analysisRunner = analysisRunner;
    }

    public AggregatedAnalysisResults analyze(AnalysisPlan analysisPlan) {
        ValidationResult validationResult = inputValidator.validate(analysisPlan);

        if (!validationResult.success()) {
            throw new InvalidConfigurationException(validationResult.message());
        }

        List<PackageWithMetrics> packages = packageListBuilder.build().stream()
                .map(pkg -> (PackageWithMetrics) pkg)
                .toList();
        var nodes = nodeBuilder.build(analysisPlan.basePackage());

        return new AggregatedAnalysisResults(
                analysisRunner.run(new AnalysisInput(packages, nodes, analysisPlan)));
    }
}
