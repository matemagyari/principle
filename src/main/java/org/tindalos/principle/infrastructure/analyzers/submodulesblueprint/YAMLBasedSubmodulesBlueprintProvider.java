package org.tindalos.principle.infrastructure.analyzers.submodulesblueprint;

import org.apache.commons.io.FileUtils;
import org.tindalos.principle.domain.analyzers.submodulesblueprint.InvalidBlueprintDefinitionException;
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmoduleDefinition;
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmoduleDefinitions;
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmoduleId;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides functionality to read and parse YAML-based submodule blueprint definitions.
 * Converts YAML configuration into SubmoduleDefinitions objects that define module
 * structure, packages, and dependencies.
 */
public class YAMLBasedSubmodulesBlueprintProvider implements SubmodulesBlueprintProvider {

    public SubmoduleDefinitions readSubmoduleDefinitions(String basePackageName, String submodulesDefinitionLocation, int violationThreshold) {
        String yaml = getYAML(submodulesDefinitionLocation);
        Map<String, Object> yamlObject = (Map<String, Object>) new Yaml().load(yaml);
        Map<String, Object> checks = (Map<String, Object>) yamlObject.get("checks");

        @SuppressWarnings("unchecked")
        Map<String, Object> modules = (Map<String, Object>) checks.get("modules");

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

    protected String getYAML(String submodulesDefinitionLocation) {
        try {
            return FileUtils.readFileToString(new File(submodulesDefinitionLocation));
        } catch (IOException ex) {
            throw new InvalidBlueprintDefinitionException("problem with reading file from " + submodulesDefinitionLocation);
        }
    }
}

