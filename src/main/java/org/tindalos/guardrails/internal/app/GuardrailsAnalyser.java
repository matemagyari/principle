package org.tindalos.guardrails.internal.app;

import java.util.List;

import org.tindalos.guardrails.internal.domain.AggregatedAnalysisResults;
import org.tindalos.guardrails.internal.domain.AnalysisRunner;
import org.tindalos.guardrails.internal.domain.constraints.exception.InvalidConfigurationException;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;
import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;

/**
 * Main application entry point for running architectural analysis.
 *
 * <p>All dependencies are injected through the constructor to keep wiring explicit.</p>
 */
public class GuardrailsAnalyser {

    private final AnalysisPlanValidator inputValidator;
    private final PackageListBuilder packageListBuilder;
    private final NodeBuilder nodeBuilder;
    private final AnalysisRunner analysisRunner;

    public GuardrailsAnalyser(
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
