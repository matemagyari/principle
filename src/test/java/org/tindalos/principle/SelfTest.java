package org.tindalos.principle;

import org.junit.Test;
import org.tindalos.principle.domain.analyzers.TestFixture;
import org.tindalos.principle.domain.analyzers.adp.ADPResult;
import org.tindalos.principle.domain.analyzers.layering.LayerViolationsResult;
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult;
import org.tindalos.principle.infrastructure.core.ConstraintsReader;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager;
import org.tindalos.principle.utils.logging.TheLogger;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class SelfTest {

    @Test
    public void checkItself() {
        ReportsDirectoryManager.ensureReportsDirectoryExists();

        TestFixture.setLogger();

        var application = PoorMansDIContainer.buildAnalyzer("org.tindalos.principle");

        var reporter = PoorMansDIContainer.createReporter();

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

}
