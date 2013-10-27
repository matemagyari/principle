package org.tindalos.principle.domain.detector.acd;

import org.tindalos.principle.domain.coredetector.ViolationsReporter;

public class ACDViolationsReporter implements ViolationsReporter<ACDResult> {

	public String report(ACDResult result) {
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("\nAverage Component Dependency violations\t");
		sb.append("\n" + sectionLine + "\n");
		sb.append(result.getACD() + "\n");
		sb.append(sectionLine + "\n");
		return sb.toString();
	}

	public Class<ACDResult> getType() {
		return ACDResult.class;
	}

}
