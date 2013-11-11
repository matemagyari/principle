package org.tindalos.principle.infrastructure.reporters;

import org.tindalos.principle.domain.coredetector.ViolationsReporter;
import org.tindalos.principle.domain.detector.acd.ACDResult;

public class ACDViolationsReporter implements ViolationsReporter<ACDResult> {

	public String report(ACDResult result) {
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("Component Dependency Metrics\t");
		sb.append("\n" + sectionLine + "\n");
		sb.append("Average Component Dependency:		" + result.getACD() + "( allowed "+result.getACDThreshold()+")\n");
		sb.append("Relative Average Component Dependency:	" + result.getRACD()  + "( allowed "+result.getRACDThreshold()+")\n");
		sb.append(sectionLine + "\n");
		return sb.toString();
	}

	public Class<ACDResult> getType() {
		return ACDResult.class;
	}

}
