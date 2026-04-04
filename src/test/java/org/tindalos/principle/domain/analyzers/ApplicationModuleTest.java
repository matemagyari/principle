package org.tindalos.principle.domain.analyzers;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.tindalos.principle.domain.analyzers.adp.ADPResult;
import org.tindalos.principle.domain.analyzers.layering.LayerViolationsResult;
import org.tindalos.principle.domain.constraints.ACD;
import org.tindalos.principle.domain.constraints.ADP;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.Grouping;
import org.tindalos.principle.domain.constraints.Layering;
import org.tindalos.principle.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.domain.constraints.SAP;
import org.tindalos.principle.domain.constraints.SDP;
import org.tindalos.principle.domain.plan.AnalysisPlan;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager;
import org.tindalos.principle.utils.logging.TheLogger;

public class ApplicationModuleTest {

    @Test
    public void checkItself() {
        ReportsDirectoryManager.ensureReportsDirectoryExists();
        var basePackage = "org.tindalos.principle";

        TestFixture.setLogger();

        var application = PoorMansDIContainer.buildAnalyzer(basePackage);

        var reporter = PoorMansDIContainer.createReporter();

        var constraints = Constraints.builder()
                .layering(layering())
                .packageCoupling(PackageCouplingConstraints.builder()
                        .sap(new SAP(0, 0.3d))
                        .adp(new ADP())
                        .sdp(new SDP())
                        .acd(new ACD())
                        .grouping(Grouping.of())
                        .build())
                .build();

        try {
            var results = application.analyze(new AnalysisPlan(constraints, basePackage));
            var summary = reporter.summary(results);
            TheLogger.info(summary);
            var adpViolated = results.adpResult().map(ADPResult::constraintViolated).orElse(false);
            var layeringViolated = results.layerViolationsResult().map(LayerViolationsResult::constraintViolated).orElse(false);
            assertFalse(adpViolated);
            assertFalse(layeringViolated);
            // assertTrue(summary.contains("analysis_summary:"));
        } catch (Exception ex) {
            TheLogger.error(ex.getMessage());
            fail(ex.getMessage());
        }
    }

    private Layering layering() {
        return new Layering(List.of("infrastructure", "app", "domain"), 0);
    }
}
