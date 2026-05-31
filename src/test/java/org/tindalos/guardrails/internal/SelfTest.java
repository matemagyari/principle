package org.tindalos.guardrails.internal;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.app.GuardrailsAnalyser;
import org.tindalos.guardrails.internal.domain.AggregatedAnalysisResults;
import org.tindalos.guardrails.internal.domain.analyzers.TestFixture;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.constraints.ConstraintsReader;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;
import org.tindalos.guardrails.internal.infrastructure.reporters.ReportsDirectoryManager;
import org.tindalos.guardrails.internal.utils.logging.TheLogger;

public class SelfTest {

    private static final AnalysisPlan PLAN = ConstraintsReader.readFromFile(Optional.of("guardrails.yml"));
    private static final GuardrailsAnalyser ANALYZER = Guardrails.createAnalyser(PLAN.basePackage());
    private static AggregatedAnalysisResults analysisResults;

    @BeforeAll
    public static void setup() {
        ReportsDirectoryManager.ensureReportsDirectoryExists();
        TestFixture.setLogger();

        analysisResults = ANALYZER.analyze(PLAN);
        TheLogger.info(Guardrails.createAggregatedYAMLReporter().summary(analysisResults));
    }

    @Test
    public void checkItself_adpConstraintIsNotViolated() {
        assertFalse(analysisResults.adpResult()
                .orElseThrow(() -> new IllegalStateException("Missing ADP analysis result"))
                .constraintViolated());
    }

    @Test
    public void checkItself_sapConstraintIsNotViolated() {
        assertFalse(analysisResults.sapResult()
                .orElseThrow(() -> new IllegalStateException("Missing SAP analysis result"))
                .constraintViolated());
    }

    @Test
    public void checkItself_sdpConstraintIsNotViolated() {
        assertFalse(analysisResults.sdpResult()
                .orElseThrow(() -> new IllegalStateException("Missing SDP analysis result"))
                .constraintViolated());
    }

    @Test
    public void checkItself_acdConstraintIsNotViolated() {
        assertFalse(analysisResults.componentDependenciesResult()
                .orElseThrow(() -> new IllegalStateException("Missing ACD analysis result"))
                .constraintViolated());
    }

    @Test
    public void checkItself_thirdPartyConstraintIsNotViolated() {
        assertFalse(analysisResults.thirdPartyViolationsResult()
                .orElseThrow(() -> new IllegalStateException("Missing third-party analysis result"))
                .constraintViolated());
    }

}
