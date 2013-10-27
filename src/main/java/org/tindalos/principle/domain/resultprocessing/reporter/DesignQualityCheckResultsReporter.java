package org.tindalos.principle.domain.resultprocessing.reporter;

import java.util.Map;

import org.tindalos.principle.domain.checker.DesignQualityCheckResults;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.ViolationsReporter;

import com.google.common.collect.Maps;

public class DesignQualityCheckResultsReporter {

	private Map<Class<? extends CheckResult>, ViolationsReporter<? extends CheckResult>> reporters;

	public DesignQualityCheckResultsReporter( Map<Class<? extends CheckResult>, ViolationsReporter<? extends CheckResult>> reporters) {
		this.reporters = Maps.newHashMap(reporters);
	}
	
	public void report(DesignQualityCheckResults results, Printer printer) {

		for (CheckResult checkResult : results.resultList()) {
			ViolationsReporter reporter = reporters.get(checkResult.getClass());
			String report = reporter.report(checkResult);

			if (checkResult.violationsDetected()) {
				printer.printWarning(report);
			} else {
				printer.printInfo(report);
			}
		}
	}
	
	public void setReporters(Map<Class<? extends CheckResult>, ViolationsReporter<? extends CheckResult>> reporters) {
		this.reporters = Maps.newHashMap(reporters);
	}

}
