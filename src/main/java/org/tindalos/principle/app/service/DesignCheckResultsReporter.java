package org.tindalos.principle.app.service;

import java.util.Map;

import org.tindalos.principle.app.service.impl.Printer;
import org.tindalos.principle.domain.checker.DesignCheckResults;
import org.tindalos.principle.domain.detector.core.CheckResult;
import org.tindalos.principle.domain.reporting.ViolationsReporter;

import com.google.common.collect.Maps;

public class DesignCheckResultsReporter {

	private Map<Class<? extends CheckResult>, ViolationsReporter<? extends CheckResult>> reporters;

	private final Printer printer;

	public DesignCheckResultsReporter(Printer printer) {
		this.printer = printer;
	}

	public void report(DesignCheckResults results) {

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
