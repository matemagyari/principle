package org.tindalos.principle.infrastructure.reporters;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tindalos.principle.domain.coredetector.ViolationsReporter;
import org.tindalos.principle.domain.detector.submodulesblueprint.Submodule;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintCheckResult;

public class SubmodulesBlueprintViolationsReporter implements ViolationsReporter<SubmodulesBlueprintCheckResult> {

	public String report(SubmodulesBlueprintCheckResult result) {
		Map<Submodule, Set<Submodule>> illegalDependencies = result.illegalDependencies();
		Map<Submodule, Set<Submodule>> missingDependencies = result.missingDependencies();
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("\tSubmodules Blueprint violations (" + result.violationsNumber() + " of the allowed " + result.getThreshold() + ")\t");
		sb.append("\n" + sectionLine + "\n");

		if (result.violationsNumber() == 0) {
			sb.append("No violations.\n");
		} else {
			for (Entry<Submodule, Set<Submodule>> entry : illegalDependencies.entrySet()) {
				sb.append(printIllegalDependencies(entry.getKey(), entry.getValue()) + "\n");
			}
			for (Entry<Submodule, Set<Submodule>> entry : missingDependencies.entrySet()) {
				sb.append(printMissingDependencies(entry.getKey(), entry.getValue()) + "\n");
			}
		}
		sb.append(sectionLine + "\n");
		return sb.toString();
	}

	private static String printIllegalDependencies(Submodule submodule, Set<Submodule> dependencies) {
		return "Illegal dependency: " + submodule + " -> " + dependencies;
	}

	private static String printMissingDependencies(Submodule submodule, Set<Submodule> dependencies) {
		return "Missing dependency: " + submodule + " -> " + dependencies;
	}

	public Class<SubmodulesBlueprintCheckResult> getType() {
		return SubmodulesBlueprintCheckResult.class;
	}

}
