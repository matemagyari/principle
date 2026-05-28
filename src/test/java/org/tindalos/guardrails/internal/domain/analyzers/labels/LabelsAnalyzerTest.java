package org.tindalos.guardrails.internal.domain.analyzers.labels;

import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelDefinition;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelGroup;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelId;
import org.tindalos.guardrails.internal.domain.constraints.labels.Labels;
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

public class LabelsAnalyzerTest {

    private final PackageStructureBuilderImpl packageStructureBuilder = new PackageStructureBuilderImpl();
    private final LabelsBuilder labelsBuilder = new LabelsBuilder(packageStructureBuilder);
    private final LabelsAnalyzer testObj = new LabelsAnalyzer(labelsBuilder);

    @Test
    public void isEnabled_withLabelsConstraint_returnsTrue() {
        var labels = new Labels(List.of(new LabelGroup("layers", Map.of(), 0)));
        var constraints = Constraints.builder().labels(labels).build();
        assertTrue(testObj.isEnabled(constraints));
    }

    @Test
    public void isEnabled_withoutLabelsConstraint_returnsFalse() {
        var constraints = Constraints.builder().build();
        assertFalse(testObj.isEnabled(constraints));
    }

    @Test
    public void analyze_noLabelsConstraint_returnsEmptyResult() {
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
        var rootPkg = pkg(basePackage, Set.of());
        var infraPkg = pkg("org.example.infra", Set.of(new PackageReference("org.example.domain")));
        var appPkg = pkg("org.example.app", Set.of());
        var domainPkg = pkg("org.example.domain", Set.of());

        // Setup Labels constraint
        // infra is defined to depend on app, but in reality depends on domain (so domain is illegal, and app is missing dependency)
        // app is defined to depend on domain, but in reality is not depending on anything (so domain is missing dependency)
        // domain has no dependencies defined (correct)
        var labelsMap = new LinkedHashMap<LabelId, LabelDefinition>();
        labelsMap.put(new LabelId("infra"), new LabelDefinition(
                new LabelId("infra"),
                Set.of(new PackageReference("org.example.infra")),
                Set.of(new LabelId("app"))
        ));
        labelsMap.put(new LabelId("app"), new LabelDefinition(
                new LabelId("app"),
                Set.of(new PackageReference("org.example.app")),
                Set.of(new LabelId("domain"))
        ));
        labelsMap.put(new LabelId("domain"), new LabelDefinition(
                new LabelId("domain"),
                Set.of(new PackageReference("org.example.domain")),
                Set.of()
        ));

        var labelGroup = new LabelGroup("layers", labelsMap, 0);
        var labels = new Labels(List.of(labelGroup));
        var constraints = Constraints.builder().labels(labels).build();
        var plan = new AnalysisPlan(constraints, basePackage);

        var packages = List.<PackageWithMetrics>of(rootPkg, infraPkg, appPkg, domainPkg);
        var input = new AnalysisInput(packages, Collections.emptySet(), plan);

        var result = testObj.analyze(input);

        assertEquals(1, result.groupResults().size());
        var groupResult = result.groupResults().get(0);
        assertEquals("layers", groupResult.name());
        assertTrue(groupResult.constraintViolated());

        // domain should be flagged as an illegal dependency of infra because infra only planned to depend on app.
        // app should be flagged as a missing dependency of infra because infra doesn't depend on app.
        // domain should be flagged as a missing dependency of app because app doesn't depend on domain.
        var illegal = groupResult.illegalDependencies();
        var missing = groupResult.missingDependencies();

        assertEquals(1, illegal.size());
        assertEquals(2, missing.size());

        var infraLabel = illegal.keySet().stream().filter(s -> s.id.value().equals("infra")).findFirst().orElseThrow();
        assertEquals(Set.of(new LabelId("domain")), illegal.get(infraLabel).stream().map(s -> s.id).collect(Collectors.toSet()));

        var appLabel = missing.keySet().stream().filter(s -> s.id.value().equals("app")).findFirst().orElseThrow();
        assertEquals(Set.of(new LabelId("domain")), missing.get(appLabel).stream().map(s -> s.id).collect(Collectors.toSet()));
    }

    private static Package pkg(String name, Set<PackageReference> ownReferences) {
        return new Package(new PackageReference(name), PackageMetrics.UNDEFINED, ownReferences, Set.of(), false, List.of());
    }
}