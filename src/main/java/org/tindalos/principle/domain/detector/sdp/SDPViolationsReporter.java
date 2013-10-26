package org.tindalos.principle.domain.detector.sdp;

import java.util.List;

import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.detector.core.ViolationsReporter;

import org.tindalos.principle.domain.core.Package;

public class SDPViolationsReporter implements ViolationsReporter<SDPResult> {

	public String report(SDPResult result) {
		List<SDPViolation> violations = result.getViolations();
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("\tStable Dependencies Principle violations\t");
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
		StringBuffer sb = new StringBuffer("\n-------------------------------");
		Package depender = violation.getDepender();
		Package dependee = violation.getDependee();
		sb.append("\n " + depender.getReference() + "["+ depender.instability()+"] ");
		sb.append(arrow);
		sb.append(" " + dependee.getReference() + "["+ dependee.instability()+"] ");
		sb.append("\n-------------------------------");
		return sb.toString();
	}

	public Class<SDPResult> getType() {
		return SDPResult.class;
	}

}
