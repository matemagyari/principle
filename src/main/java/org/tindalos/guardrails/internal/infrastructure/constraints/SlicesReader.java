package org.tindalos.guardrails.internal.infrastructure.constraints;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.domain.constraints.slices.InvalidSliceDefinitionException;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceDefinition;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceGroup;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceId;
import org.tindalos.guardrails.internal.domain.constraints.slices.Slices;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.infrastructure.core.ConstraintDefinitionReader;

/**
 * Provides functionality to read and parse YAML-based slices definitions under constraints.
 */
public class SlicesReader implements ConstraintDefinitionReader<Slices> {

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Slices> read(Map<String, Object> yamlObject) {
        var constraints = Optional.ofNullable((Map<String, Object>) yamlObject.get("constraints"));
        var slicesObject = constraints.map(section -> section.get("slices"));
        if (slicesObject.isEmpty() || !(slicesObject.get() instanceof List)) {
            return Optional.empty();
        }

        var basePackageName = Optional.ofNullable((String) yamlObject.get("root_package"))
                .orElseThrow(() -> new InvalidSliceDefinitionException("Root package not defined!"));

        List<Object> rawGroups = (List<Object>) slicesObject.get();
        List<SliceGroup> groups = new ArrayList<>();

        for (Object rawGroup : rawGroups) {
            if (rawGroup instanceof Map) {
                Map<String, Object> groupMap = (Map<String, Object>) rawGroup;
                String name = (String) groupMap.getOrDefault("name", "Unnamed Group");
                int violationThreshold = (Integer) groupMap.getOrDefault("violation_threshold", 0);

                Map<SliceId, SliceDefinition> definitionMap = buildSliceDefinitions(basePackageName, groupMap);
                addDependencies(groupMap, definitionMap);

                groups.add(new SliceGroup(name, definitionMap, violationThreshold));
            }
        }

        return Optional.of(new Slices(groups));
    }

    private void checkSliceExists(Set<SliceId> validSlices, SliceId sliceId) {
        if (!validSlices.contains(sliceId)) {
            throw new InvalidSliceDefinitionException("No slice defined with id " + sliceId.value());
        }
    }

    private List<SliceId> transformToSliceIds(List<String> dependencies, Set<SliceId> validSlices) {
        List<SliceId> ids = dependencies.stream()
                .map(SliceId::new)
                .collect(Collectors.toList());

        ids.forEach(id -> checkSliceExists(validSlices, id));
        return ids;
    }

    private void addDependencies(Map<String, Object> groupMap, Map<SliceId, SliceDefinition> definitionMap) {
        Object dependenciesObj = groupMap.get("dependencies");
        if (dependenciesObj == null) {
            throw new InvalidSliceDefinitionException("Dependencies not defined for slice group: " + groupMap.get("name"));
        }

        @SuppressWarnings("unchecked")
        Map<String, List<String>> dependencies = (Map<String, List<String>>) dependenciesObj;

        dependencies.forEach((key, value) -> {
            SliceId sliceId = new SliceId(key);
            checkSliceExists(definitionMap.keySet(), sliceId);
            SliceDefinition sliceDefinition = definitionMap.get(sliceId);
            List<SliceId> plannedDependencies = transformToSliceIds(value, definitionMap.keySet());
            
            SliceDefinition updatedDefinition = new SliceDefinition(
                    sliceDefinition.id(),
                    sliceDefinition.packages(),
                    new HashSet<>(plannedDependencies)
            );
            definitionMap.put(sliceId, updatedDefinition);
        });
    }

    private Map<SliceId, SliceDefinition> buildSliceDefinitions(String basePackageName, Map<String, Object> groupMap) {
        Object slicesObj = groupMap.get("slices");
        if (slicesObj == null) {
            throw new InvalidSliceDefinitionException("Slices mapping not defined for slice group: " + groupMap.get("name"));
        }

        @SuppressWarnings("unchecked")
        Map<String, List<String>> slicesMapping = (Map<String, List<String>>) slicesObj;

        Map<SliceId, SliceDefinition> definitionMap = new java.util.LinkedHashMap<>();
        slicesMapping.entrySet().forEach(entry -> {
            SliceId sliceId = new SliceId(entry.getKey());
            List<String> packageNames = entry.getValue();
            Set<PackageReference> packages = packageNames.stream()
                    .map(name -> new PackageReference(basePackageName + "." + name))
                    .collect(Collectors.toSet());
            definitionMap.put(sliceId, new SliceDefinition(sliceId, packages, Set.of()));
        });
        return definitionMap;
    }
}
