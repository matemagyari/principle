package org.tindalos.principle.infrastructure.reporters;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.coredetector.ViolationsReporter;
import org.tindalos.principle.domain.detector.sdp.SDPResult;
import org.tindalos.principle.domain.detector.sdp.SDPViolation;

public class SDPViolationsReporter implements ViolationsReporter<SDPResult> {

	public String report(SDPResult result) {
		List<SDPViolation> violations = result.getViolations();
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("\tStable Dependencies Principle violations ("+violations.size()+" of allowed "+result.getThreshold()+" )\t");
		sb.append("\n" + sectionLine + "\n");

		if (violations.isEmpty()) {
			sb.append("No violations.\n");
		} else {
			for (SDPViolation cycle : violations) {
				sb.append(print(cycle) + "\n");
			}
		}
		sb.append(sectionLine + "\n");
		return sb.toString();
	}

	private String print(SDPViolation violation) {

		String arrow = "-->";
		StringBuffer sb = new StringBuffer("");
		Package depender = violation.getDepender();
		Package dependee = violation.getDependee();
		sb.append("\n " + depender.getReference() + "["+ depender.instability()+"] ");
		sb.append(arrow);
		sb.append(" " + dependee.getReference() + "["+ dependee.instability()+"] ");
		return sb.toString();
	}

	public Class<SDPResult> getType() {
		return SDPResult.class;
	}

}
