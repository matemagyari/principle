package org.tindalos.principle.domain.detector.adp;

import java.util.List;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.detector.core.ViolationsReporter;

public class APDViolationsReporter implements ViolationsReporter<APDResult> {

	public String report(APDResult result) {
		List<Cycle> cycles = result.getCycles();
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("\tAcyclic Package Dependency violations\t");
		sb.append("\n" + sectionLine + "\n");

		if (cycles.isEmpty()) {
			sb.append("No violations.\n");
		} else {
			for (Cycle cycle : cycles) {
				sb.append(print(cycle) + "\n");
			}
		}
		sb.append(sectionLine + "\n");
		return sb.toString();
	}

	private String print(Cycle cycle) {

		String arrow = "-->";
		StringBuffer sb = new StringBuffer("\n-------------------------------");
		for (PackageReference reference : cycle.getReferences()) {
			sb.append("\n" + reference + " " + arrow);
		}
		sb.append("\n-------------------------------");
		return sb.toString();
	}

	public Class<APDResult> getType() {
		return APDResult.class;
	}

}
