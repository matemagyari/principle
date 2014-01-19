package org.tindalos.principle.infrastructure.detector.submodulesblueprint;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.tindalos.principle.domain.core.ListConverter;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.detector.submodulesblueprint.InvalidBlueprintDefinitionException;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinition;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinitions;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinitionsProvider;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleId;
import org.tindalos.principle.domain.expectations.SubmodulesDefinitionLocation;
import org.yaml.snakeyaml.Yaml;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class YAMLBasedSubmodulesBlueprintProvider extends YAMLBasedSubmodulesBlueprintProviderScala {

	public SubmoduleDefinitions readSubmoduleDefinitions(SubmodulesDefinitionLocation submodulesDefinitionLocation, String basePackageName) {
		String yaml = getYAML(submodulesDefinitionLocation);
		return processYAML(yaml,basePackageName);
	}

	@SuppressWarnings("unchecked")
	private static SubmoduleDefinitions processYAML(String yamlText, String basePackageName) {
		
		Yaml yaml = new Yaml();
		Map<String, Object> yamlObject = (Map<String, Object>) yaml.load(yamlText);
		
		Map<SubmoduleId, SubmoduleDefinition> submoduleDefinitionMap = buildSubmoduleDefinitions(yamlObject, basePackageName);
		
		addDependencies(yamlObject, submoduleDefinitionMap);
		
		return new SubmoduleDefinitions(ListConverter.convert(submoduleDefinitionMap));
	
	}

	@SuppressWarnings("unchecked")
	private static void addDependencies(Map<String, Object> yamlObject, Map<SubmoduleId, SubmoduleDefinition> submoduleDefinitionMap) {
		
		Map<String, List<String>> dependencies = (Map<String,  List<String>>) yamlObject.get("subdmodule-dependencies");
		if (dependencies == null) {
			throw new InvalidBlueprintDefinitionException("Submodule dependencies not defined! ");
		}
		for (Entry<String,  List<String>> entry : dependencies.entrySet()) {
			SubmoduleId submoduleId = new SubmoduleId(entry.getKey());
			
			checkSubmoduleExists(submoduleDefinitionMap.keySet(), submoduleId);
			SubmoduleDefinition submoduleDefinition = submoduleDefinitionMap.get(submoduleId);
			
			Collection<SubmoduleId> plannedDependencies = transformToSubmoduleIds(entry.getValue(),submoduleDefinitionMap.keySet());
			submoduleDefinition.addPlannedDependencies(ListConverter.convert(plannedDependencies));
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<SubmoduleId, SubmoduleDefinition> buildSubmoduleDefinitions(Map<String, Object> yamlObject, String basePackageName) {
		Map<SubmoduleId, SubmoduleDefinition> submoduleDefinitionMap = Maps.newHashMap();
		
		Map<String, List<String>> definitions = (Map<String,  List<String>>) yamlObject.get("subdmodule-definitions");
		if (definitions == null) {
			throw new InvalidBlueprintDefinitionException("Submodules not defined! ");
		}
		for (Entry<String,  List<String>> entry : definitions.entrySet()) {
			SubmoduleId submoduleId = new SubmoduleId(entry.getKey());
			
			Set<PackageReference> packages = transformToPackageReferences(entry.getValue(), basePackageName);
			SubmoduleDefinition submoduleDefinition = new SubmoduleDefinition(submoduleId, ListConverter.convert(packages));
			
			submoduleDefinitionMap.put(submoduleId, submoduleDefinition);
		}
		return submoduleDefinitionMap;
	}

	private static Collection<SubmoduleId> transformToSubmoduleIds(List<String> dependencies, Set<SubmoduleId> validSubmodules)  {
		
		Set<SubmoduleId> submodules = Sets.newHashSet();
		for (String submoduleName : dependencies) {
			SubmoduleId submoduleId = new SubmoduleId(submoduleName);
			checkSubmoduleExists(validSubmodules, submoduleId);
			submodules.add(submoduleId);
		}
		return submodules;
	}

	private static void checkSubmoduleExists(Set<SubmoduleId> validSubmodules, SubmoduleId submoduleId) {
		if (!validSubmodules.contains(submoduleId)) throw new InvalidBlueprintDefinitionException("No submodules defined with id " + submoduleId);
	}

	private static Set<PackageReference> transformToPackageReferences(List<String> packageNames, String basePackageName) {
		Set<PackageReference> packages = Sets.newHashSet();
		for(String packageName : packageNames) {
			packages.add(new PackageReference(basePackageName + "." + packageName));
		}
		return packages;
	}

	private static String getYAML(SubmodulesDefinitionLocation submodulesDefinitionLocation) {
		try {
			return FileUtils.readFileToString(new File(submodulesDefinitionLocation.filePath()));
		} catch (IOException e) {
			throw new InvalidBlueprintDefinitionException("problem with reading file from " + submodulesDefinitionLocation.filePath());
		}
	}

}
