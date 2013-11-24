package org.tindalos.principle.infrastructure.detector.submodulesblueprint;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.detector.submodulesblueprint.InvalidBlueprintDefinitionException;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinition;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinitions;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinitionsProvider;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleId;
import org.tindalos.principle.domain.expectations.SubmodulesDefinitionLocation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class JSONBasedSubmodulesBlueprintProvider implements SubmoduleDefinitionsProvider {

	public SubmoduleDefinitions readSubmoduleDefinitions(SubmodulesDefinitionLocation submodulesDefinitionLocation) {
		String json = getJSON(submodulesDefinitionLocation);
		return processJSON(json);
	}

	private static SubmoduleDefinitions processJSON(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			
			Map<SubmoduleId, SubmoduleDefinition> submoduleDefinitionMap = builSubmoduleDefinitions(jsonObject);
			
			addDependencies(jsonObject, submoduleDefinitionMap);
			
			return new SubmoduleDefinitions(submoduleDefinitionMap);

		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static void addDependencies(JSONObject jsonObject, Map<SubmoduleId, SubmoduleDefinition> submoduleDefinitionMap) throws JSONException {
		JSONObject dependencies = jsonObject.getJSONObject("subdmoduledependencies");
		if (dependencies == null) {
			throw new InvalidBlueprintDefinitionException("Submodule dependencies not defined! ");
		}
		Iterator<String> keys = dependencies.keys();
		while(keys.hasNext()) {
			
			SubmoduleId submoduleId = new SubmoduleId(keys.next());
			
			Collection<SubmoduleId> plannedDependencies = transformToSubmoduleIds(dependencies.getJSONArray(submoduleId.value()));
			SubmoduleDefinition submoduleDefinition = submoduleDefinitionMap.get(submoduleId);
			
			if (submoduleDefinition == null) {
				throw new InvalidBlueprintDefinitionException("Submodule not defined: " + submoduleId);
			}
			submoduleDefinition.addPlannedDependencies(plannedDependencies);
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<SubmoduleId, SubmoduleDefinition> builSubmoduleDefinitions(JSONObject jsonObject) throws JSONException {
		Map<SubmoduleId, SubmoduleDefinition> submoduleDefinitionMap = Maps.newHashMap();
		
		JSONObject definitions = jsonObject.getJSONObject("subdmoduledefinitions");
		if (definitions == null) {
			throw new InvalidBlueprintDefinitionException("Submodules not defined! ");
		}
		Iterator<String> keys = definitions.keys();
		while(keys.hasNext()) {
			SubmoduleId submoduleId = new SubmoduleId(keys.next());
			
			Set<PackageReference> packages = transformToPackageReferences(definitions.getJSONArray(submoduleId.value()));
			SubmoduleDefinition submoduleDefinition = new SubmoduleDefinition(submoduleId, packages);
			
			submoduleDefinitionMap.put(submoduleId, submoduleDefinition);
		}

		return submoduleDefinitionMap;
	}

	private static Collection<SubmoduleId> transformToSubmoduleIds(JSONArray dependencies) throws JSONException {
		Set<SubmoduleId> submodules = Sets.newHashSet();
		for (int i = 0; i < dependencies.length(); i++) {
			submodules.add(new SubmoduleId(dependencies.getString(i)));
		}
		return submodules;
	}

	private static Set<PackageReference> transformToPackageReferences(JSONArray packageNames) throws JSONException {
		Set<PackageReference> packages = Sets.newHashSet();
		for (int i = 0; i < packageNames.length(); i++) {
			packages.add(new PackageReference(packageNames.getString(i)));
		}
		return packages;
	}

	private static String getJSON(SubmodulesDefinitionLocation submodulesDefinitionLocation) {
		try {
			return FileUtils.readFileToString(new File(submodulesDefinitionLocation.filePath()));
		} catch (IOException e) {
			throw new InvalidBlueprintDefinitionException("problem with reading file from " + submodulesDefinitionLocation.filePath());
		}
	}

}
