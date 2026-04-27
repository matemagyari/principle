package org.tindalos.principle.internal.domain.analyzers.submodulesblueprint;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.tindalos.principle.internal.domain.constraints.submodules.Overlap;
import org.tindalos.principle.internal.domain.constraints.submodules.OverlappingSubmoduleDefinitionsException;
import org.tindalos.principle.internal.domain.plan.AnalysisInput;
import org.tindalos.principle.internal.domain.constraints.Constraints;
import org.tindalos.principle.internal.domain.plan.AnalysisPlan;
import org.tindalos.principle.internal.domain.constraints.submodules.SubmoduleDefinition;
import org.tindalos.principle.internal.domain.constraints.submodules.SubmoduleDefinitions;
import org.tindalos.principle.internal.domain.constraints.submodules.SubmoduleId;
import org.tindalos.principle.internal.domain.core.packages.PackageMetrics;
import org.tindalos.principle.internal.domain.core.packages.PackageReference;
import org.tindalos.principle.internal.domain.core.packages.PackageWithMetrics;

/**
 * Unit tests for SubmodulesBlueprintAnalyzer covering enablement and analysis outcomes.
 */
public class SubmodulesBlueprintAnalyzerTest {

    @Test
    public void isEnabled_submoduleDefinitionsMissing_returnsFalse() {
        var analyzer = new SubmodulesBlueprintAnalyzer(new NeverCalledSubmodulesBuilder());

        boolean enabled = analyzer.isEnabled(Constraints.builder().build());

        assertFalse(enabled);
    }

    @Test
    public void isEnabled_submoduleDefinitionsPresent_returnsTrue() {
        var analyzer = new SubmodulesBlueprintAnalyzer(new NeverCalledSubmodulesBuilder());

        boolean enabled = analyzer.isEnabled(
                Constraints.builder().submoduleDefinitions(definitions(0)).build());

        assertTrue(enabled);
    }

    @Test
    public void analyze_submoduleDefinitionsMissing_returnsEmptyResult() {
        var analyzer = new SubmodulesBlueprintAnalyzer(new NeverCalledSubmodulesBuilder());

        SubmodulesBlueprintAnalysisResult result = analyzer.analyze(inputWithoutSubmodules());

        assertEquals(0, result.threshold());
        assertTrue(result.illegalDependencies().isEmpty());
        assertTrue(result.missingDependencies().isEmpty());
        assertTrue(result.overlaps().isEmpty());
        assertFalse(result.constraintViolated());
    }

    @Test
    public void analyze_validSubmodules_collectsIllegalAndMissingDependencies() {
        var mod1 = submodule("MOD1",
                Set.of(pkg("com.example.mod1", "com.example.mod3")),
                Set.of(new SubmoduleId("MOD2")));
        var mod2 = submodule("MOD2", Set.of(pkg("com.example.mod2")), Set.of());
        var mod3 = submodule("MOD3", Set.of(pkg("com.example.mod3")), Set.of());

        var analyzer = new SubmodulesBlueprintAnalyzer(
                new ReturningSubmodulesBuilder(Set.of(mod1, mod2, mod3)));

        SubmodulesBlueprintAnalysisResult result = analyzer.analyze(inputWithSubmodules(0));

        assertEquals(0, result.threshold());
        assertEquals(Map.of(mod1, Set.of(mod3)), result.illegalDependencies());
        assertEquals(Map.of(mod1, Set.of(mod2)), result.missingDependencies());
        assertTrue(result.overlaps().isEmpty());
        assertTrue(result.constraintViolated());
    }

    @Test
    public void analyze_builderThrowsOverlapException_returnsOverlapResult() {
        Set<Overlap> overlaps = Set.of(new Overlap(new SubmoduleId("MOD1"), new SubmoduleId("MOD2")));
        var analyzer = new SubmodulesBlueprintAnalyzer(new ThrowingSubmodulesBuilder(overlaps));

        SubmodulesBlueprintAnalysisResult result = analyzer.analyze(inputWithSubmodules(2));

        assertEquals(2, result.threshold());
        assertTrue(result.illegalDependencies().isEmpty());
        assertTrue(result.missingDependencies().isEmpty());
        assertEquals(overlaps, result.overlaps());
        assertFalse(result.constraintViolated());
    }

    private static AnalysisInput inputWithoutSubmodules() {
        var constraints = Constraints.builder().build();
        return new AnalysisInput(List.of(), Set.of(), new AnalysisPlan(constraints, "com.example"));
    }

    private static AnalysisInput inputWithSubmodules(int threshold) {
        var constraints = Constraints.builder().submoduleDefinitions(definitions(threshold)).build();
        return new AnalysisInput(List.of(), Set.of(), new AnalysisPlan(constraints, "com.example"));
    }

    private static SubmoduleDefinitions definitions(int threshold) {
        var mod1 = new SubmoduleDefinition(
                new SubmoduleId("MOD1"),
                Set.of(new PackageReference("com.example.mod1")));
        return new SubmoduleDefinitions(Map.of(new SubmoduleId("MOD1"), mod1), threshold);
    }

    private static Submodule submodule(String id, Set<PackageWithMetrics> packages, Set<SubmoduleId> plannedDeps) {
        return new Submodule(new SubmoduleId(id), packages, plannedDeps);
    }

    private static PackageWithMetrics pkg(String name, String... efferentRefs) {
        Set<PackageReference> refs = new java.util.HashSet<>();
        for (String ref : efferentRefs) {
            refs.add(new PackageReference(ref));
        }

        return new PackageWithMetrics() {
            @Override
            public PackageReference reference() {
                return new PackageReference(name);
            }

            @Override
            public PackageMetrics getMetrics() {
                return new PackageMetrics(0, 0, 0, 0, 0);
            }

            @Override
            public Set<PackageReference> getOwnPackageReferences() {
                return Set.of();
            }

            @Override
            public Set<PackageReference> getOwnExternalPackageReferences() {
                return Set.of();
            }

            @Override
            public Set<PackageReference> accumulatedDirectPackageReferences() {
                return Set.copyOf(refs);
            }
        };
    }

    private static final class NeverCalledSubmodulesBuilder extends SubmodulesBuilder {
        private NeverCalledSubmodulesBuilder() {
            super(null);
        }

        @Override
        public Set<Submodule> build(
                SubmoduleDefinitions submoduleDefinitions,
                List<PackageWithMetrics> packages,
                String basePackageName) {
            throw new AssertionError("SubmodulesBuilder should not have been called");
        }
    }

    private static final class ReturningSubmodulesBuilder extends SubmodulesBuilder {
        private final Set<Submodule> submodules;

        private ReturningSubmodulesBuilder(Set<Submodule> submodules) {
            super(null);
            this.submodules = submodules;
        }

        @Override
        public Set<Submodule> build(
                SubmoduleDefinitions submoduleDefinitions,
                List<PackageWithMetrics> packages,
                String basePackageName) {
            return submodules;
        }
    }

    private static final class ThrowingSubmodulesBuilder extends SubmodulesBuilder {
        private final Set<Overlap> overlaps;

        private ThrowingSubmodulesBuilder(Set<Overlap> overlaps) {
            super(null);
            this.overlaps = overlaps;
        }

        @Override
        public Set<Submodule> build(
                SubmoduleDefinitions submoduleDefinitions,
                List<PackageWithMetrics> packages,
                String basePackageName) {
            throw new OverlappingSubmoduleDefinitionsException(overlaps);
        }
    }
}