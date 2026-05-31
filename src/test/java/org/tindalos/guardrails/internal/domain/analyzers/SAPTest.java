package org.tindalos.guardrails.internal.domain.analyzers;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.sap.SAPResult;
import org.tindalos.guardrails.internal.domain.analyzers.sap.SAPViolationAnalyzer;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.internal.domain.constraints.SAP;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;

public class SAPTest {

    @BeforeEach
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void emptyRootPackage_isExcludedFromSapOutliers() {
        var root = pkg("root", 0.0f, 0.0f, 0.9f);
        var violating = pkg("root.violating", 0.1f, 0.3f, 0.6f);
        var healthy = pkg("root.healthy", 0.5f, 0.5f, 0.0f);

        var result = runProgrammatic(List.of(root, violating, healthy), 0, 0.5);

        assertEquals(1, result.outlierPackages().size());
        assertEquals(new PackageReference("root.violating"), result.outlierPackages().getFirst().reference());
        assertTrue(result.constraintViolated());
    }

    @Test
    public void nonEmptyRootPackage_isIncludedInSapAnalysis() {
        var root = pkg("root", 0.7f, 0.2f, 0.6f);
        var child = pkg("root.child", 0.4f, 0.5f, 0.1f);

        var result = runProgrammatic(List.of(root, child), 0, 0.5);

        assertEquals(1, result.outlierPackages().size());
        assertEquals(new PackageReference("root"), result.outlierPackages().getFirst().reference());
    }

    @Test
    public void thresholdBoundary_equalNumberOfOutliers_isNotViolated() {
        var root = pkg("root", 0.0f, 0.0f, 0.0f);
        var violating = pkg("root.violating", 0.2f, 0.2f, 0.7f);

        var result = runProgrammatic(List.of(root, violating), 1, 0.5);

        assertEquals(1, result.outlierPackages().size());
        assertFalse(result.constraintViolated());
    }

    private SAPResult runProgrammatic(List<Package> packages, int threshold, double maxDistance) {
        var constraints = Constraints.builder()
                .packageCoupling(PackageCouplingConstraints.builder().sap(new SAP(threshold, maxDistance)).build())
                .build();
        var plan = new AnalysisPlan(constraints, "root");
        var input = new AnalysisInput(packages, Set.of(), plan);
        return new SAPViolationAnalyzer().analyze(input);
    }

    private static Package pkg(String packageName, float abstractness, float instability, float distance) {
        return new Package(
                new PackageReference(packageName),
                new PackageMetrics(0, 0, abstractness, instability, distance),
                Set.of(),
                Set.of(),
                false);
    }
}

