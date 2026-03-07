package org.tindalos.principle.infrastructure.reporters;

import org.junit.Test;
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesResult;
import org.tindalos.principle.domain.constraints.NCCD;
import org.tindalos.principle.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.domain.constraints.RACD;
import static org.junit.Assert.assertEquals;
import static org.tindalos.principle.infrastructure.reporters.YamlAssertions.assertValidYaml;

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
                    component_dependency:
                      average: 2.0
                      relative_average: 0.4
                      normalized_cumulative: 0.4
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
                    component_dependency:
                      average: 2.0
                      relative_average: 0.4
                      normalized_cumulative: 0.4
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
                    component_dependency:
                      average: 2.0
                      relative_average: 0.4
                      normalized_cumulative: 0.4
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
                    component_dependency:
                      average: 2.0
                      relative_average: 0.4
                      normalized_cumulative: 0.4
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
                    component_dependency:
                      average: 1.0
                      relative_average: 1.0
                      normalized_cumulative: 1.0
                    num_of_components: 1
                  racd_threshold: ~
                  nccd_threshold: ~
                """;
        assertEquals(expected, report);
    }
}
