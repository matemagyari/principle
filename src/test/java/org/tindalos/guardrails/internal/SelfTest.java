package org.tindalos.guardrails.internal;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.api.AnalysisOutcome;
import org.tindalos.guardrails.api.AnalysisPlan;
import org.tindalos.guardrails.api.GuardrailsAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.TestFixture;
import org.tindalos.guardrails.internal.domain.analyzers.adp.ADPResult;
import org.tindalos.guardrails.internal.domain.analyzers.slices.SlicesAnalysisResult;
import org.tindalos.guardrails.internal.infrastructure.constraints.ConstraintsReader;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;
import org.tindalos.guardrails.internal.infrastructure.reporters.ReportsDirectoryManager;
import org.tindalos.guardrails.internal.utils.logging.TheLogger;

public class SelfTest {

    @Test
    public void checkItselfFromInternalClasses() {
        ReportsDirectoryManager.ensureReportsDirectoryExists();

        TestFixture.setLogger();

        var application = Guardrails.createAnalyser("org.tindalos.guardrails");

        var reporter = Guardrails.createAggregatedYAMLReporter();

        var plan = ConstraintsReader.readFromFile(Optional.of("guardrails.yml"));

        try {
            var results = application.analyze(plan);
            var summary = reporter.summary(results);
            TheLogger.info(summary);
            var adpViolated = results.adpResult().map(ADPResult::constraintViolated).orElse(false);
            var slicesViolated = results.slicesAnalysisResult().map(SlicesAnalysisResult::constraintViolated).orElse(false);
            assertFalse(adpViolated);
            assertFalse(slicesViolated);
        } catch (Exception ex) {
            TheLogger.error(ex.getMessage());
            fail(ex.getMessage());
        }
    }

    @Test
    public void checkItselfFromAPI() {
        TestFixture.setLogger();

        AnalysisPlan plan = org.tindalos.guardrails.api.Guardrails.readPlan(Optional.of("guardrails.yml"));
        GuardrailsAnalyzer analyzer = org.tindalos.guardrails.api.Guardrails.analyzer("org.tindalos.guardrails");
        AnalysisOutcome outcome = analyzer.analyze(plan);

        assertFalse(outcome.hasViolations());
        TheLogger.info(outcome.summaryYaml());
    }

}
