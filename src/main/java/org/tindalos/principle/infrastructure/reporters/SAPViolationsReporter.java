package org.tindalos.principle.infrastructure.reporters;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.coredetector.ViolationsReporter;
import org.tindalos.principle.domain.detector.sap.SAPResult;

public class SAPViolationsReporter implements ViolationsReporter<SAPResult> {

	public String report(SAPResult result) {
		List<Package> outlierPackages = result.getOutlierPackages();
		String sectionLine = "==============================================================";
		StringBuffer sb = new StringBuffer("\n" + sectionLine + "\n");
		sb.append("\tStable Abstractions Principle violations ("+outlierPackages.size()+" of allowed "+result.getThreshold()+" )\t");
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
