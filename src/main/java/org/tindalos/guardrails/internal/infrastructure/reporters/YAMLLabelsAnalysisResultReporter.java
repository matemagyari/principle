package org.tindalos.guardrails.internal.infrastructure.reporters;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.app.reporters.LabelsAnalysisResultReporter;
import org.tindalos.guardrails.internal.domain.analyzers.labels.Label;
import org.tindalos.guardrails.internal.domain.analyzers.labels.LabelGroupResult;
import org.tindalos.guardrails.internal.domain.analyzers.labels.LabelsAnalysisResult;

/**
 * Reports labels analysis results in structured YAML format.
 */
public class YAMLLabelsAnalysisResultReporter implements LabelsAnalysisResultReporter {

    @Override
    public Class<LabelsAnalysisResult> resultType() {
        return LabelsAnalysisResult.class;
    }

    @Override
    public String report(LabelsAnalysisResult result) {
        var header = """
                labels_result:
                  description: Labels constraints
                  constraint_violated: %s
                """.formatted(result.constraintViolated());

        if (result.groupResults().isEmpty()) {
            return header + "  groups: []\n";
        }

        var groupsYaml = result.groupResults().stream()
                .map(this::groupYaml)
                .collect(Collectors.joining());

        return header + "  groups:\n" + groupsYaml;
    }

    private String groupYaml(LabelGroupResult group) {
        var sb = new StringBuilder();
        sb.append("    - name: ").append(group.name()).append("\n");
        sb.append("      violation_count: ").append(group.violationsNumber()).append("\n");
        sb.append("      threshold: ").append(group.violationThreshold()).append("\n");
        sb.append("      constraint_violated: ").append(group.constraintViolated()).append("\n");

        if (!group.overlaps().isEmpty()) {
            sb.append("      overlaps: true\n");
        } else {
            sb.append(indent(dependenciesYaml("illegal_dependencies", group.illegalDependencies()), "      "));
            sb.append(indent(dependenciesYaml("missing_dependencies", group.missingDependencies()), "      "));
        }
        return sb.toString();
    }

    private String dependenciesYaml(String key, Map<Label, Set<Label>> deps) {
        if (deps.isEmpty()) {
            return key + ": []\n";
        }

        var lines = deps.entrySet().stream()
                .sorted(java.util.Comparator.comparing(e -> e.getKey().id.value()))
                .map(e -> {
                    var depList = e.getValue().stream()
                            .map(s -> s.id.value())
                            .sorted()
                            .collect(Collectors.joining(", "));
                    return """
                            - label: %s
                              depends_on: [%s]
                            """.formatted(e.getKey().id.value(), depList);
                })
                .collect(Collectors.joining());

        return key + ":\n" + indent(lines, "  ");
    }

    private String indent(String text, String prefix) {
        if (text.isEmpty()) {
            return "";
        }
        return java.util.Arrays.stream(text.split("\\n"))
                .map(line -> {
                    if (line.isBlank()) {
                        return "";
                    }
                    return prefix + line;
                })
                .collect(Collectors.joining("\n")) + "\n";
    }
}
