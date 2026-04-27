package org.tindalos.principle.internal.infrastructure.reporters;

import org.tindalos.principle.internal.app.reporters.SubmodulesBlueprintAnalysisResultReporter;
import org.tindalos.principle.internal.domain.analyzers.submodulesblueprint.Submodule;
import org.tindalos.principle.internal.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reports Submodules Blueprint analysis results in YAML format.
 * Produces a structured, machine-readable representation of illegal and missing
 * submodule dependencies, suitable for further processing or integration with other tools.
 */
public class YAMLSubmodulesBlueprintAnalysisResultReporter implements SubmodulesBlueprintAnalysisResultReporter {

    @Override
    public String report(SubmodulesBlueprintAnalysisResult result) {
        var header = """
                submodules_blueprint_result:
                  description: Submodules Blueprint constraint
                  violation_count: %s
                  threshold: %s
                  constraint_violated: %s
                """.formatted(result.violationsNumber(), result.threshold(), result.constraintViolated());

        if (!result.overlaps().isEmpty())
            return header + "  overlaps: true\n";
        else
            return header + violationsYaml(result);
    }

    private String violationsYaml(SubmodulesBlueprintAnalysisResult result) {
        if (result.violationsNumber() == 0)
            return """
                      illegal_dependencies: []
                      missing_dependencies: []
                    """;

        return dependenciesYaml("illegal_dependencies", result.illegalDependencies())
                + dependenciesYaml("missing_dependencies", result.missingDependencies());
    }

    private String dependenciesYaml(String key, Map<Submodule, Set<Submodule>> deps) {
        if (deps.isEmpty())
            return "  " + key + ": []\n";

        var lines = deps.entrySet().stream()
                .sorted(java.util.Comparator.comparing(e -> e.getKey().id.value()))
                .map(e -> {
                    var depList = e.getValue().stream()
                            .map(s -> s.id.value())
                            .sorted()
                            .collect(Collectors.joining(", "));
                    return """
                                - submodule: %s
                                  depends_on: [%s]
                            """.formatted(e.getKey().id.value(), depList);
                })
                .collect(Collectors.joining());

        return "  " + key + ":\n" + lines;
    }
}

