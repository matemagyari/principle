package org.tindalos.principle.infrastructure.reporters;

import org.junit.Test;
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesResult;
import org.tindalos.principle.domain.constraints.NCCD;
import org.tindalos.principle.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.domain.constraints.RACD;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for YAMLComponentDependencyAnalysisResultReporter verifying correct YAML output
 * for various ACD analysis scenarios.
 */
public class YAMLComponentDependencyAnalysisResultReporterTest {

    private final YAMLComponentDependencyAnalysisResultReporter reporter =
            new YAMLComponentDependencyAnalysisResultReporter();

    private PackageCouplingConstraints couplingWithRacd(double racdThreshold) {
        return PackageCouplingConstraints.builder().racd(new RACD(racdThreshold)).build();
    }

    private PackageCouplingConstraints couplingWithRacdAndNccd(double racdThreshold, double nccdThreshold) {
        return PackageCouplingConstraints.builder()
                .racd(new RACD(racdThreshold))
                .nccd(new NCCD(nccdThreshold))
                .build();
    }

    private PackageCouplingConstraints couplingWithNoThresholds() {
        return PackageCouplingConstraints.builder().build();
    }

    private void assertValidYaml(String yaml) {
        assertNotNull("YAML output must not be null", yaml);
        Object parsed = new Yaml().load(yaml);
        assertNotNull("YAML must parse to a non-null object", parsed);
    }

    @Test
    public void noThresholds_reportsNullThresholds() {
        var result = new ComponentDependenciesResult(10, 5, couplingWithNoThresholds());

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                component_dependency_result:
                  description: Average Component Dependency constraint
                  constraint_violated: false
                  metrics:
                    acd: 2.0
                    racd: 0.4
                    nccd: 0.4
                    num_of_components: 5
                  racd_threshold: ~
                  nccd_threshold: ~
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withRacdThreshold_notViolated() {
        var result = new ComponentDependenciesResult(10, 5, couplingWithRacd(0.5));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                component_dependency_result:
                  description: Average Component Dependency constraint
                  constraint_violated: false
                  metrics:
                    acd: 2.0
                    racd: 0.4
                    nccd: 0.4
                    num_of_components: 5
                  racd_threshold: 0.5
                  nccd_threshold: ~
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withRacdThreshold_violated() {
        var result = new ComponentDependenciesResult(10, 5, couplingWithRacd(0.3));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                component_dependency_result:
                  description: Average Component Dependency constraint
                  constraint_violated: true
                  metrics:
                    acd: 2.0
                    racd: 0.4
                    nccd: 0.4
                    num_of_components: 5
                  racd_threshold: 0.3
                  nccd_threshold: ~
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withRacdAndNccdThresholds_reportsAll() {
        var result = new ComponentDependenciesResult(10, 5, couplingWithRacdAndNccd(0.5, 0.6));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                component_dependency_result:
                  description: Average Component Dependency constraint
                  constraint_violated: false
                  metrics:
                    acd: 2.0
                    racd: 0.4
                    nccd: 0.4
                    num_of_components: 5
                  racd_threshold: 0.5
                  nccd_threshold: 0.6
                """;
        assertEquals(expected, report);
    }

    @Test
    public void singleComponent_acdIsOne() {
        var result = new ComponentDependenciesResult(1, 1, couplingWithNoThresholds());

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                component_dependency_result:
                  description: Average Component Dependency constraint
                  constraint_violated: false
                  metrics:
                    acd: 1.0
                    racd: 1.0
                    nccd: 1.0
                    num_of_components: 1
                  racd_threshold: ~
                  nccd_threshold: ~
                """;
        assertEquals(expected, report);
    }
}
