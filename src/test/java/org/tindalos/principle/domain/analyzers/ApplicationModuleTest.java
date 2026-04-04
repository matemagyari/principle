package org.tindalos.principle.domain.analyzers;

import org.junit.Test;
import org.tindalos.principle.domain.constraints.ACD;
import org.tindalos.principle.domain.constraints.ADP;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.Grouping;
import org.tindalos.principle.domain.constraints.Layering;
import org.tindalos.principle.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.domain.constraints.SAP;
import org.tindalos.principle.domain.constraints.SDP;
import org.tindalos.principle.domain.core.AnalysisPlan;
import org.tindalos.principle.infrastructure.ConsolePrinter;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager;

import java.util.List;

import static org.junit.Assert.fail;

public class ApplicationModuleTest {

    @Test
    public void checkItself() {
        ReportsDirectoryManager.ensureReportsDirectoryExists();
        var basePackage = "org.tindalos.principle";

        TestFixture.setLogger();

        var application = PoorMansDIContainer.buildAnalyzer(basePackage, new ConsolePrinter());

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
            application.analyze(new AnalysisPlan(constraints, basePackage));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private Layering layering() {
        return new Layering(List.of("infrastructure", "app", "domain"), 0);
    }
}
