package org.tindalos.guardrails.internal.infrastructure.reporters;

import org.junit.Test;
import org.tindalos.guardrails.internal.domain.analyzers.thirdparty.ThirdPartyViolationsResult;
import org.tindalos.guardrails.internal.domain.constraints.ThirdParty;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.tindalos.guardrails.internal.infrastructure.reporters.YamlAssertions.assertValidYaml;

/**
 * Tests for YAMLThirdPartyAnalysisResultReporter verifying correct YAML output
 * for various third-party dependency analysis scenarios.
 */
public class YAMLThirdPartyAnalysisResultReporterTest {

    private final YAMLThirdPartyAnalysisResultReporter reporter = new YAMLThirdPartyAnalysisResultReporter();
    private final ThirdParty thirdParty = new ThirdParty(Collections.emptyList(), 0);

    private PackageReference ref(String name) {
        return new PackageReference(name);
    }

    private ThirdPartyViolationsResult result(Map<PackageReference, Set<PackageReference>> violations) {
        return new ThirdPartyViolationsResult(violations, thirdParty);
    }

    private ThirdPartyViolationsResult result(Map<PackageReference, Set<PackageReference>> violations, int threshold) {
        return new ThirdPartyViolationsResult(violations, new ThirdParty(Collections.emptyList(), threshold));
    }

    @Test
    public void noViolations_reportsEmptyViolations() {
        var report = reporter.report(result(Map.of()));

        assertValidYaml(report);

        var expected = """
                third_party_result:
                  description: Third Party dependency constraint
                  violation_count: 0
                  threshold: 0
                  constraint_violated: false
                  violations: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withThreshold_reportsThreshold() {
        var report = reporter.report(result(Map.of(), 3));

        assertValidYaml(report);

        var expected = """
                third_party_result:
                  description: Third Party dependency constraint
                  violation_count: 0
                  threshold: 3
                  constraint_violated: false
                  violations: []
                """;
        assertEquals(expected, report);
    }

    @Test
    public void singleViolation_reportsReferrerAndDependency() {
        var report = reporter.report(result(Map.of(
                ref("com.example.app"), Set.of(ref("org.apache.commons.io")))));

        assertValidYaml(report);

        var expected = """
                third_party_result:
                  description: Third Party dependency constraint
                  violation_count: 1
                  threshold: 0
                  constraint_violated: true
                  violations:
                    - referrer: com.example.app
                      dependency: org.apache.commons.io
                """;
        assertEquals(expected, report);
    }

    @Test
    public void withinThreshold_constraintNotViolated() {
        var report = reporter.report(result(Map.of(
                ref("com.example.app"), Set.of(ref("org.apache.commons.io"))), 5));

        assertValidYaml(report);

        var expected = """
                third_party_result:
                  description: Third Party dependency constraint
                  violation_count: 1
                  threshold: 5
                  constraint_violated: false
                  violations:
                    - referrer: com.example.app
                      dependency: org.apache.commons.io
                """;
        assertEquals(expected, report);
    }

    @Test
    public void multipleReferrers_reportedAlphabetically() {
        var report = reporter.report(result(Map.of(
                ref("com.example.domain"), Set.of(ref("org.apache.commons.lang3")),
                ref("com.example.app"),    Set.of(ref("org.apache.commons.io")))));

        assertValidYaml(report);

        var expected = """
                third_party_result:
                  description: Third Party dependency constraint
                  violation_count: 2
                  threshold: 0
                  constraint_violated: true
                  violations:
                    - referrer: com.example.app
                      dependency: org.apache.commons.io
                    - referrer: com.example.domain
                      dependency: org.apache.commons.lang3
                """;
        assertEquals(expected, report);
    }

    @Test
    public void oneReferrerWithMultipleDependencies_eachListedSeparately() {
        var report = reporter.report(result(Map.of(
                ref("com.example.app"), Set.of(
                        ref("org.apache.commons.io"),
                        ref("org.apache.commons.lang3")))));

        assertValidYaml(report);

        var expected = """
                third_party_result:
                  description: Third Party dependency constraint
                  violation_count: 2
                  threshold: 0
                  constraint_violated: true
                  violations:
                    - referrer: com.example.app
                      dependency: org.apache.commons.io
                    - referrer: com.example.app
                      dependency: org.apache.commons.lang3
                """;
        assertEquals(expected, report);
    }
}

