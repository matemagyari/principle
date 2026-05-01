package org.tindalos.guardrails.internal.domain.analyzers.acd;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;
import org.tindalos.guardrails.internal.domain.constraints.ACD;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.internal.domain.constraints.RACD;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuilder;
import org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Unit tests for ComponentDependenciesAnalyzer.
 */
public class ComponentDependenciesAnalyzerTest {

    @Test
    public void isEnabled_withoutCouplingConstraints_returnsFalse() {
        var analyzer = new ComponentDependenciesAnalyzer(new ReturningBuilder(pkg("com.example.base", Set.of(), metrics(1, 0))));

        boolean enabled = analyzer.isEnabled(Constraints.builder().build());

        assertFalse(enabled);
    }

    @Test
    public void isEnabled_withRacdConstraint_returnsTrue() {
        var analyzer = new ComponentDependenciesAnalyzer(new ReturningBuilder(pkg("com.example.base", Set.of(), metrics(1, 0))));
        var constraints = Constraints.builder()
                .packageCoupling(PackageCouplingConstraints.builder().racd(new RACD(0.5)).build())
                .build();

        boolean enabled = analyzer.isEnabled(constraints);

        assertTrue(enabled);
    }

    @Test
    public void analyze_whenBasePackageIsNotIsolated_usesAllPackages() {
        var base = pkg("com.example", Set.of(), metrics(1, 0));
        var mod1 = pkg("com.example.mod1", Set.of("com.example.mod2"), metrics(0, 1));
        var mod2 = pkg("com.example.mod2", Set.of(), metrics(1, 0));

        var analyzer = new ComponentDependenciesAnalyzer(new ReturningBuilder(base));
        var input = input(List.of(base, mod1, mod2));

        ComponentDependenciesResult result = analyzer.analyze(input);

        assertEquals(5, result.cumulatedComponentDependency());
        assertEquals(3, result.numOfComponents());
        assertEquals(5.0 / 3.0, result.acd(), 0.0001);
    }

    @Test
    public void analyze_whenBasePackageIsIsolated_excludesBasePackageFromCalculation() {
        var base = pkg("com.example", Set.of(), metrics(0, 0));
        var mod1 = pkg("com.example.mod1", Set.of("com.example.mod2"), metrics(0, 1));
        var mod2 = pkg("com.example.mod2", Set.of(), metrics(1, 0));

        var analyzer = new ComponentDependenciesAnalyzer(new ReturningBuilder(base));
        var input = input(List.of(base, mod1, mod2));

        ComponentDependenciesResult result = analyzer.analyze(input);

        assertEquals(3, result.cumulatedComponentDependency());
        assertEquals(2, result.numOfComponents());
        assertEquals(1.5, result.acd(), 0.0001);
    }

    private static AnalysisInput input(List<PackageWithMetrics> packages) {
        var constraints = Constraints.builder()
                .packageCoupling(PackageCouplingConstraints.builder().acd(new ACD()).build())
                .build();
        return new AnalysisInput(packages, Set.of(), new AnalysisPlan(constraints, "com.example"));
    }

    private static PackageMetrics metrics(int afferent, int efferent) {
        return new PackageMetrics(afferent, efferent, 0, 0, 0);
    }

    private static TestPackage pkg(String name, Set<String> ownRefs, PackageMetrics metrics) {
        var references = ownRefs.stream().map(PackageReference::new).collect(java.util.stream.Collectors.toSet());
        return new TestPackage(name, references, metrics);
    }

    private static final class ReturningBuilder implements PackageStructureBuilder {
        private final Package basePackage;

        private ReturningBuilder(Package basePackage) {
            this.basePackage = basePackage;
        }

        @Override
        public Package build(List<Package> packages, String basePackageName) {
            for (Package aPackage : packages) {
                if (!aPackage.equals(basePackage)) {
                    basePackage.insert(aPackage);
                }
            }
            return basePackage;
        }
    }

    private static final class TestPackage extends Package {
        private final Set<PackageReference> ownReferences;
        private final PackageMetrics packageMetrics;

        private TestPackage(String referenceName, Set<PackageReference> ownReferences, PackageMetrics packageMetrics) {
            super(referenceName);
            this.ownReferences = new HashSet<>(ownReferences);
            this.packageMetrics = packageMetrics;
        }

        @Override
        public PackageMetrics getMetrics() {
            return packageMetrics;
        }

        @Override
        public Set<PackageReference> getOwnPackageReferences() {
            return Set.copyOf(ownReferences);
        }

        @Override
        public Set<PackageReference> getOwnExternalPackageReferences() {
            return Set.of();
        }

        @Override
        public boolean isUnreferred() {
            return false;
        }
    }
}
