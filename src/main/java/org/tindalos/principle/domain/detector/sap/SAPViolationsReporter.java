package org.tindalos.principle.domain.detector.sap;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.detector.core.ViolationsReporter;

public class SAPViolationsReporter implements ViolationsReporter<SAPResult> {

	public String report(SAPResult result) {
		List<Package> outlierPackages = result.getOutlierPackages();
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("\tStable Abstractions Principle violations\t");
		sb.append("\n" + sectionLine + "\n");

		if (outlierPackages.isEmpty()) {
			sb.append("No violations.\n");
		} else {
			for (Package outlierPackage : outlierPackages) {
				sb.append(print(outlierPackage) + "\n");
			}
		}
		sb.append(sectionLine + "\n");
		return sb.toString();
	}

	private String print(Package outlierPackage) {
		return outlierPackage + " " + outlierPackage.distance();
	}

	public Class<SAPResult> getType() {
		return SAPResult.class;
	}

}
