package org.tindalos.principle.internal;

import org.junit.Test;
import org.tindalos.principle.api.AnalysisOutcome;
import org.tindalos.principle.api.AnalysisPlan;
import org.tindalos.principle.api.PrincipleAnalyzer;
import org.tindalos.principle.internal.domain.analyzers.TestFixture;
import org.tindalos.principle.internal.domain.analyzers.adp.ADPResult;
import org.tindalos.principle.internal.domain.analyzers.layering.LayerViolationsResult;
import org.tindalos.principle.internal.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult;
import org.tindalos.principle.internal.infrastructure.core.ConstraintsReader;
import org.tindalos.principle.internal.infrastructure.di.Principle;
import org.tindalos.principle.internal.infrastructure.reporters.ReportsDirectoryManager;
import org.tindalos.principle.internal.utils.logging.TheLogger;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class SelfTest {

    @Test
    public void checkItselfFromInternalClasses() {
        ReportsDirectoryManager.ensureReportsDirectoryExists();

        TestFixture.setLogger();

        var application = Principle.createAnalyser("org.tindalos.principle");

        var reporter = Principle.createAggregatedYAMLReporter();

        var plan = ConstraintsReader.readFromFile(Optional.of("principle.yml"));

        try {
            var results = application.analyze(plan);
            var summary = reporter.summary(results);
            TheLogger.info(summary);
            var adpViolated = results.adpResult().map(ADPResult::constraintViolated).orElse(false);
            var layeringViolated = results.layerViolationsResult().map(LayerViolationsResult::constraintViolated).orElse(false);
            var submodulesViolated = results.submodulesBlueprintAnalysisResult().map(SubmodulesBlueprintAnalysisResult::constraintViolated).orElse(false);
            assertFalse(adpViolated);
            assertFalse(layeringViolated);
            assertFalse(submodulesViolated);
        } catch (Exception ex) {
            TheLogger.error(ex.getMessage());
            fail(ex.getMessage());
        }
    }

    @Test
    public void checkItselfFromAPI() {
        ReportsDirectoryManager.ensureReportsDirectoryExists();

        TestFixture.setLogger();

        AnalysisPlan plan = org.tindalos.principle.api.Principle.readPlan(Optional.of("principle.yml"));
        PrincipleAnalyzer analyzer = org.tindalos.principle.api.Principle.analyzer("org.tindalos.principle");
        AnalysisOutcome outcome = analyzer.analyze(plan);

        assertFalse(outcome.hasViolations());
        TheLogger.info(outcome.summaryYaml());
    }

}
