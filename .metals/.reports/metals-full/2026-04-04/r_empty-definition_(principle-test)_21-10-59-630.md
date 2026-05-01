error id: file://<WORKSPACE>/src/test/java/org/tindalos/principle/domain/analyzers/ApplicationModuleTest.java:org/tindalos/principle/domain/analyzers/ApplicationModuleTest#`<init>`().
file://<WORKSPACE>/src/test/java/org/tindalos/principle/domain/analyzers/ApplicationModuleTest.java
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol org/tindalos/principle/domain/analyzers/ApplicationModuleTest#`<init>`().
empty definition using fallback
non-local guesses:

offset: 1105
uri: file://<WORKSPACE>/src/test/java/org/tindalos/principle/domain/analyzers/ApplicationModuleTest.java
text:
```scala
package org.tindalos.guardrails.domain.analyzers;

import org.junit.Test;
import org.tindalos.guardrails.domain.AggregatedAnalysisResults;
import org.tindalos.guardrails.domain.constraints.ACD;
import org.tindalos.guardrails.domain.constraints.ADP;
import org.tindalos.guardrails.domain.constraints.Constraints;
import org.tindalos.guardrails.domain.constraints.Grouping;
import org.tindalos.guardrails.domain.constraints.Layering;
import org.tindalos.guardrails.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.domain.constraints.SAP;
import org.tindalos.guardrails.domain.constraints.SDP;
import org.tindalos.guardrails.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.app.reporters.AnalysisResultsReporter;
import org.tindalos.guardrails.infrastructure.ConsolePrinter;
import org.tindalos.guardrails.infrastructure.di.PoorMansDIContainer;
import org.tindalos.guardrails.infrastructure.reporters.ReportsDirectoryManager;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class ApplicationModuleTest@@ {

    @Test
    public void checkItself() {
        ReportsDirectoryManager.ensureReportsDirectoryExists();
        var basePackage = "org.tindalos.guardrailss";

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
            String summary = reporter.summary(results);
            assertFalse(results.hasViolations());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private Layering layering() {
        return new Layering(List.of("infrastructure", "app", "domain"), 0);
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 