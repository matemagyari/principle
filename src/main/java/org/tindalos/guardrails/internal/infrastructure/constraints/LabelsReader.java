package org.tindalos.guardrails.internal.infrastructure.constraints;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.domain.constraints.labels.InvalidLabelDefinitionException;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelDefinition;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelGroup;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelId;
import org.tindalos.guardrails.internal.domain.constraints.labels.Labels;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.infrastructure.core.ConstraintDefinitionReader;

/**
 * Provides functionality to read and parse YAML-based labels definitions under constraints.
 */
public class LabelsReader implements ConstraintDefinitionReader<Labels> {

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Labels> read(Map<String, Object> yamlObject) {
        var constraints = Optional.ofNullable((Map<String, Object>) yamlObject.get("constraints"));
        var labelsObject = constraints.map(section -> section.get("labels"));
        if (labelsObject.isEmpty() || !(labelsObject.get() instanceof List)) {
            return Optional.empty();
        }

        var basePackageName = Optional.ofNullable((String) yamlObject.get("root_package"))
                .orElseThrow(() -> new InvalidLabelDefinitionException("Root package not defined!"));

        List<Object> rawGroups = (List<Object>) labelsObject.get();
        List<LabelGroup> groups = new ArrayList<>();

        for (Object rawGroup : rawGroups) {
            if (rawGroup instanceof Map) {
                Map<String, Object> groupMap = (Map<String, Object>) rawGroup;
                String name = (String) groupMap.getOrDefault("name", "Unnamed Group");
                int violationThreshold = (Integer) groupMap.getOrDefault("violation_threshold", 0);

                Map<LabelId, LabelDefinition> definitionMap = buildLabelDefinitions(basePackageName, groupMap);
                addDependencies(groupMap, definitionMap);

                groups.add(new LabelGroup(name, definitionMap, violationThreshold));
            }
        }

        return Optional.of(new Labels(groups));
    }

    private void checkLabelExists(Set<LabelId> validLabels, LabelId labelId) {
        if (!validLabels.contains(labelId)) {
            throw new InvalidLabelDefinitionException("No label defined with id " + labelId.value());
        }
    }

    private List<LabelId> transformToLabelIds(List<String> dependencies, Set<LabelId> validLabels) {
        List<LabelId> ids = dependencies.stream()
                .map(LabelId::new)
                .collect(Collectors.toList());

        ids.forEach(id -> checkLabelExists(validLabels, id));
        return ids;
    }

    private void addDependencies(Map<String, Object> groupMap, Map<LabelId, LabelDefinition> definitionMap) {
        Object dependenciesObj = groupMap.get("dependencies");
        if (dependenciesObj == null) {
            throw new InvalidLabelDefinitionException("Dependencies not defined for label group: " + groupMap.get("name"));
        }

        @SuppressWarnings("unchecked")
        Map<String, List<String>> dependencies = (Map<String, List<String>>) dependenciesObj;

        dependencies.forEach((key, value) -> {
            LabelId labelId = new LabelId(key);
            checkLabelExists(definitionMap.keySet(), labelId);
            LabelDefinition labelDefinition = definitionMap.get(labelId);
            List<LabelId> plannedDependencies = transformToLabelIds(value, definitionMap.keySet());
            
            LabelDefinition updatedDefinition = new LabelDefinition(
                    labelDefinition.id(),
                    labelDefinition.packages(),
                    new HashSet<>(plannedDependencies)
            );
            definitionMap.put(labelId, updatedDefinition);
        });
    }

    private Map<LabelId, LabelDefinition> buildLabelDefinitions(String basePackageName, Map<String, Object> groupMap) {
        Object labelsObj = groupMap.get("labels");
        if (labelsObj == null) {
            throw new InvalidLabelDefinitionException("Labels mapping not defined for label group: " + groupMap.get("name"));
        }

        @SuppressWarnings("unchecked")
        Map<String, List<String>> labelsMapping = (Map<String, List<String>>) labelsObj;

        Map<LabelId, LabelDefinition> definitionMap = new java.util.LinkedHashMap<>();
        labelsMapping.entrySet().forEach(entry -> {
            LabelId labelId = new LabelId(entry.getKey());
            List<String> packageNames = entry.getValue();
            Set<PackageReference> packages = packageNames.stream()
                    .map(name -> new PackageReference(basePackageName + "." + name))
                    .collect(Collectors.toSet());
            definitionMap.put(labelId, new LabelDefinition(labelId, packages, Set.of()));
        });
        return definitionMap;
    }
}
