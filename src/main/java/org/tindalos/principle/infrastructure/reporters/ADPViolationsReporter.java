package org.tindalos.principle.infrastructure.reporters;

import java.util.List;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.coredetector.ViolationsReporter;
import org.tindalos.principle.domain.detector.adp.ADPResult;

public class ADPViolationsReporter implements ViolationsReporter<ADPResult> {

	public String report(ADPResult result) {
		List<Cycle> cycles = result.getCycles();
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("\tAcyclic Package Dependency Principle violations ("+cycles.size()+" of the allowed "+result.getThreshold()+")\t");
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
		StringBuffer sb = new StringBuffer();
		for (PackageReference reference : cycle.getReferences()) {
			sb.append("\n" + reference + " " + arrow);
		}
		sb.append("\n-------------------------------");
		return sb.toString();
	}

	public Class<ADPResult> getType() {
		return ADPResult.class;
	}

}
