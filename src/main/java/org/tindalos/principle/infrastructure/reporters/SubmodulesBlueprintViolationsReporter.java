package org.tindalos.principle.infrastructure.reporters;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tindalos.principle.domain.coredetector.ViolationsReporter;
import org.tindalos.principle.domain.detector.submodulesblueprint.Submodule;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintCheckResult;

public class SubmodulesBlueprintViolationsReporter implements ViolationsReporter<SubmodulesBlueprintCheckResult> {

	public String report(SubmodulesBlueprintCheckResult result) {
		Map<Submodule, Set<Submodule>> violations = result.getViolations();
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("\tSubmodules Blueprint violations ("+violations.size()+" of the allowed "+result.getThreshold()+")\t");
		sb.append("\n" + sectionLine + "\n");

		if (violations.isEmpty()) {
			sb.append("No violations.\n");
		} else {
			for (Entry<Submodule, Set<Submodule>> entry : violations.entrySet() ) {
				sb.append(print(entry.getKey(), entry.getValue()) + "\n");
			}
		}
		sb.append(sectionLine + "\n");
		return sb.toString();
	}

	private String print(Submodule submodule, Set<Submodule> dependencies) {
		return submodule + " -> " + dependencies;
	}

	public Class<SubmodulesBlueprintCheckResult> getType() {
		return SubmodulesBlueprintCheckResult.class;
	}

}
