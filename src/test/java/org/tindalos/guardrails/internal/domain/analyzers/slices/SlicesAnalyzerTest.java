package org.tindalos.guardrails.internal.domain.analyzers.slices;

import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceDefinition;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceGroup;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceId;
import org.tindalos.guardrails.internal.domain.constraints.slices.Slices;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;
import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.di.PackageStructureBuilderImpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SlicesAnalyzerTest {

    private final PackageStructureBuilderImpl packageStructureBuilder = new PackageStructureBuilderImpl();
    private final SlicesBuilder slicesBuilder = new SlicesBuilder(packageStructureBuilder);
    private final SlicesAnalyzer testObj = new SlicesAnalyzer(slicesBuilder);

    @Test
    public void isEnabled_withSlicesConstraint_returnsTrue() {
        var slices = new Slices(List.of(new SliceGroup("layers", Map.of(), 0)));
        var constraints = Constraints.builder().slices(slices).build();
        assertTrue(testObj.isEnabled(constraints));
    }

    @Test
    public void isEnabled_withoutSlicesConstraint_returnsFalse() {
        var constraints = Constraints.builder().build();
        assertFalse(testObj.isEnabled(constraints));
    }

    @Test
    public void analyze_noSlicesConstraint_returnsEmptyResult() {
        var basePackage = "org.example";
        var plan = new AnalysisPlan(Constraints.builder().build(), basePackage);
        var input = new AnalysisInput(List.of(), Collections.emptySet(), plan);

        var result = testObj.analyze(input);
        assertTrue(result.groupResults().isEmpty());
    }

    @Test
    public void analyze_withIllegalAndMissingDependencies() {
        var basePackage = "org.example";

        // Setup base package and subpackages
        var rootPkg = new TestPackage(basePackage, Set.of());
        var infraPkg = new TestPackage("org.example.infra", Set.of(new PackageReference("org.example.domain")));
        var appPkg = new TestPackage("org.example.app", Set.of());
        var domainPkg = new TestPackage("org.example.domain", Set.of());

        // Setup Slices constraint
        // infra is defined to depend on app, but in reality depends on domain (so domain is illegal, and app is missing dependency)
        // app is defined to depend on domain, but in reality is not depending on anything (so domain is missing dependency)
        // domain has no dependencies defined (correct)
        var slicesMap = new LinkedHashMap<SliceId, SliceDefinition>();
        slicesMap.put(new SliceId("infra"), new SliceDefinition(
                new SliceId("infra"),
                Set.of(new PackageReference("org.example.infra")),
                Set.of(new SliceId("app"))
        ));
        slicesMap.put(new SliceId("app"), new SliceDefinition(
                new SliceId("app"),
                Set.of(new PackageReference("org.example.app")),
                Set.of(new SliceId("domain"))
        ));
        slicesMap.put(new SliceId("domain"), new SliceDefinition(
                new SliceId("domain"),
                Set.of(new PackageReference("org.example.domain")),
                Set.of()
        ));

        var sliceGroup = new SliceGroup("layers", slicesMap, 0);
        var slices = new Slices(List.of(sliceGroup));
        var constraints = Constraints.builder().slices(slices).build();
        var plan = new AnalysisPlan(constraints, basePackage);

        var packages = List.<PackageWithMetrics>of(rootPkg, infraPkg, appPkg, domainPkg);
        var input = new AnalysisInput(packages, Collections.emptySet(), plan);

        var result = testObj.analyze(input);

        assertEquals(1, result.groupResults().size());
        var groupResult = result.groupResults().get(0);
        assertEquals("layers", groupResult.name());
        assertTrue(groupResult.constraintViolated());

        // domain should be flagged as an illegal dependency of infra because infra only planed to depend on app.
        // app should be flagged as a missing dependency of infra because infra doesn't depend on app.
        // domain should be flagged as a missing dependency of app because app doesn't depend on domain.
        var illegal = groupResult.illegalDependencies();
        var missing = groupResult.missingDependencies();

        assertEquals(1, illegal.size());
        assertEquals(2, missing.size());

        var infraSlice = illegal.keySet().stream().filter(s -> s.id.value().equals("infra")).findFirst().orElseThrow();
        assertEquals(Set.of(new SliceId("domain")), illegal.get(infraSlice).stream().map(s -> s.id).collect(Collectors.toSet()));

        var appSlice = missing.keySet().stream().filter(s -> s.id.value().equals("app")).findFirst().orElseThrow();
        assertEquals(Set.of(new SliceId("domain")), missing.get(appSlice).stream().map(s -> s.id).collect(Collectors.toSet()));
    }

    private static final class TestPackage extends Package {
        private final Set<PackageReference> ownReferences;

        private TestPackage(String referenceName, Set<PackageReference> ownReferences) {
            super(referenceName);
            this.ownReferences = new HashSet<>(ownReferences);
        }

        @Override
        public PackageMetrics getMetrics() {
            return null;
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