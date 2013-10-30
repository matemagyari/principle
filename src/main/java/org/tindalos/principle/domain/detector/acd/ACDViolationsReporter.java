package org.tindalos.principle.domain.detector.acd;

import org.tindalos.principle.domain.coredetector.ViolationsReporter;

public class ACDViolationsReporter implements ViolationsReporter<ACDResult> {

	public String report(ACDResult result) {
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("\nComponent Dependency Metrics\t");
		sb.append("\n" + sectionLine + "\n");
		sb.append("Average Component Dependency:		" + result.getACD() + "\n");
		sb.append("Relative Average Component Dependency:	" + result.getRACD() + "\n");
		sb.append(sectionLine + "\n");
		return sb.toString();
	}

	public Class<ACDResult> getType() {
		return ACDResult.class;
	}

}
