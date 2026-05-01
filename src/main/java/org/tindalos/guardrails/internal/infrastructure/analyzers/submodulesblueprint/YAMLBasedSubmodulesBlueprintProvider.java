package org.tindalos.guardrails.internal.infrastructure.analyzers.submodulesblueprint;

import org.tindalos.guardrails.internal.domain.constraints.submodules.InvalidBlueprintDefinitionException;
import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleDefinition;
import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleDefinitions;
import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleId;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides functionality to read and parse YAML-based submodule blueprint definitions.
 * Converts YAML configuration into SubmoduleDefinitions objects that define module
 * structure, packages, and dependencies.
 */
public class YAMLBasedSubmodulesBlueprintProvider implements SubmodulesBlueprintProvider {

    @Override
    @SuppressWarnings("unchecked")
    public SubmoduleDefinitions readSubmoduleDefinitions(Map<String, Object> yamlObject) {
    var basePackageName = Optional.ofNullable((String) yamlObject.get("root_package"))
        .orElseThrow(() -> new InvalidBlueprintDefinitionException("Root package not defined!"));
    var constraints = Optional.ofNullable((Map<String, Object>) yamlObject.get("constraints"))
        .orElseThrow(() -> new InvalidBlueprintDefinitionException("Constraints not defined!"));
    var modules = Optional.ofNullable((Map<String, Object>) constraints.get("modules"))
        .orElseThrow(() -> new InvalidBlueprintDefinitionException("Submodules not defined!"));
    var violationThreshold = Optional.ofNullable((Integer) modules.get("violation_threshold")).orElse(0);

    Map<SubmoduleId, SubmoduleDefinition> submoduleDefinitionMap = buildSubmoduleDefinitions(basePackageName, modules);
        addDependencies(modules, submoduleDefinitionMap);

    return new SubmoduleDefinitions(submoduleDefinitionMap, violationThreshold);
    }

    private void checkSubmoduleExists(Set<SubmoduleId> validSubmodules, SubmoduleId submoduleId) {
        if (!validSubmodules.contains(submoduleId)) {
            throw new InvalidBlueprintDefinitionException("No submodules defined with id " + submoduleId);
        }
    }

    private List<SubmoduleId> transformToSubmoduleIds(List<String> dependencies, Set<SubmoduleId> validSubmodules) {
        List<SubmoduleId> ids = dependencies.stream()
                .map(SubmoduleId::new)
                .collect(Collectors.toList());

        ids.forEach(id -> checkSubmoduleExists(validSubmodules, id));
        return ids;
    }

    private void addDependencies(Map<String, Object> yamlObject, Map<SubmoduleId, SubmoduleDefinition> submoduleDefinitionMap) {
        Object dependenciesObj = yamlObject.get("module-dependencies");
        if (dependenciesObj == null) {
            throw new InvalidBlueprintDefinitionException("Submodule dependencies not defined!");
        }

        @SuppressWarnings("unchecked")
        Map<String, List<String>> dependencies = (Map<String, List<String>>) dependenciesObj;

        dependencies.forEach((key, value) -> {
            SubmoduleId submoduleId = new SubmoduleId(key);
            checkSubmoduleExists(submoduleDefinitionMap.keySet(), submoduleId);
            SubmoduleDefinition submoduleDefinition = submoduleDefinitionMap.get(submoduleId);
            List<SubmoduleId> plannedDependencies = transformToSubmoduleIds(value, submoduleDefinitionMap.keySet());
            submoduleDefinition.addPlannedDependencies(plannedDependencies);
        });
    }

    private Map<SubmoduleId, SubmoduleDefinition> buildSubmoduleDefinitions(String basePackageName, Map<String, Object> yamlObject) {
        Object definitionsObj = yamlObject.get("module-definitions");
        if (definitionsObj == null) {
            throw new InvalidBlueprintDefinitionException("Submodules not defined!");
        }

        @SuppressWarnings("unchecked")
        Map<String, List<String>> definitions = (Map<String, List<String>>) definitionsObj;

        return definitions.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> new SubmoduleId(entry.getKey()),
                        entry -> {
                            SubmoduleId submoduleId = new SubmoduleId(entry.getKey());
                            List<String> packageNames = entry.getValue();
                            Set<PackageReference> packages = packageNames.stream()
                                    .map(name -> new PackageReference(basePackageName + "." + name))
                                    .collect(Collectors.toSet());
                            return new SubmoduleDefinition(submoduleId, packages);
                        }
                ));
    }

}

