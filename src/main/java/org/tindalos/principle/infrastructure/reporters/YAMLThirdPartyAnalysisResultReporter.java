package org.tindalos.principle.infrastructure.reporters;

import org.tindalos.principle.app.reporters.ThirdPartyAnalysisResultReporter;
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyViolationsResult;
import org.tindalos.principle.domain.core.packages.PackageReference;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reports Third Party dependency analysis results in YAML format.
 * Produces a structured, machine-readable representation of disallowed third-party
 * dependency violations, suitable for further processing or integration with other tools.
 */
public class YAMLThirdPartyAnalysisResultReporter implements ThirdPartyAnalysisResultReporter {

    @Override
    public String report(ThirdPartyViolationsResult result) {
        int violationCount = result.violations().values().stream().mapToInt(Set::size).sum();
        return """
                third_party_result:
                  description: Third Party dependency constraint
                  violation_count: %s
                  threshold: %s
                  constraint_violated: %s
                %s""".formatted(
                violationCount,
                result.threshold(),
                result.constraintViolated(),
                violationsYaml(result));
    }

    private String violationsYaml(ThirdPartyViolationsResult result) {
        if (result.violations().isEmpty()) {
            return "  violations: []\n";
        }

        var violationLines = result.violations().entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .flatMap(entry -> entry.getValue().stream()
                        .sorted()
                        .map(dep -> """
                                    - referrer: %s
                                      dependency: %s
                                """.formatted(entry.getKey().name(), dep.name())))
                .collect(Collectors.joining());

        return "  violations:\n" + violationLines;
    }
}

