package org.tindalos.guardrails.internal.domain.analyzers;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.sdp.SDPViolation;
import org.tindalos.guardrails.internal.domain.analyzers.sdp.SDPViolationAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.sdp.SDPResult;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.internal.domain.constraints.SDP;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;

public class SDPTest {

    @BeforeEach
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple() {
        var result = run("org.tindalos.guardrailstest.sdp");
        assertEquals(3, result.violations().size());
        assertTrue(result.constraintViolated());
        assertTrue(result.violations().contains(new SDPViolation(
                pkg("org.tindalos.guardrailstest.sdp.stable1", 0.0f),
                pkg("org.tindalos.guardrailstest.sdp.stable2", 0.0f))));
        assertTrue(result.violations().contains(new SDPViolation(
                pkg("org.tindalos.guardrailstest.sdp.stable1", 0.0f),
                pkg("org.tindalos.guardrailstest.sdp.stable3", 0.0f))));
        assertTrue(result.violations().contains(new SDPViolation(
                pkg("org.tindalos.guardrailstest.sdp.stable2", 0.0f),
                pkg("org.tindalos.guardrailstest.sdp.stable3", 0.0f))));
    }

    @Test
    public void constraintIsNotViolatedWhenThresholdAllowsAllDetectedViolations() {
        var result = run("org.tindalos.guardrailstest.sdp", 3);
        assertEquals(3, result.violations().size());
        assertFalse(result.constraintViolated());
    }

    @Test
    public void programmaticInput_ignoresMissingReferences_andOnlyFlagsMoreUnstableDependees() {
        var depender = pkg("root.depender", 0.4f, Set.of("root.moreUnstable", "root.missing", "root.moreStable"));
        var moreUnstable = pkg("root.moreUnstable", 0.8f);
        var moreStable = pkg("root.moreStable", 0.1f);
        var root = pkg("root", 0.0f);

        var result = runProgrammatic(List.of(root, depender, moreUnstable, moreStable), 0);

        assertEquals(1, result.violations().size());
        var violation = result.violations().getFirst();
        assertEquals(new PackageReference("root.depender"), violation.depender().reference());
        assertEquals(new PackageReference("root.moreUnstable"), violation.dependee().reference());
    }

    private SDPResult run(String basePackage) {
        return run(basePackage, 0);
    }

    private SDPResult run(String basePackage, int threshold) {
        var constraints = Constraints.builder()
                .packageCoupling(PackageCouplingConstraints.builder().sdp(new SDP(threshold)).build())
                .build();
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Guardrails.createAnalyser(basePackage);
        return analyzer.analyze(plan).sdpResult().get();
    }

    private SDPResult runProgrammatic(List<Package> packages, int threshold) {
        var constraints = Constraints.builder()
                .packageCoupling(PackageCouplingConstraints.builder().sdp(new SDP(threshold)).build())
                .build();
        var plan = new AnalysisPlan(constraints, "root");
        var input = new AnalysisInput(packages, Set.of(), plan);
        return new SDPViolationAnalyzer().analyze(input);
    }

    private static Package pkg(String packageName, float instability) {
        return pkg(packageName, instability, Set.of());
    }

    private static Package pkg(String packageName, float instability, Set<String> references) {
        return new Package(
                new PackageReference(packageName),
                new PackageMetrics(0, 0, 0.0f, instability, 0.0f),
                references.stream().map(PackageReference::new).collect(java.util.stream.Collectors.toUnmodifiableSet()),
                Set.of(),
                false);
    }
}
