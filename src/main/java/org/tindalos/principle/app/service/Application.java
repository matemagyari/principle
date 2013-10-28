package org.tindalos.principle.app.service;

import org.tindalos.principle.domain.checker.DesignQualityCheckResults;
import org.tindalos.principle.domain.checker.DesignQualityCheckService;
import org.tindalos.principle.domain.core.DesignQualityCheckParameters;
import org.tindalos.principle.domain.resultprocessing.reporter.DesignQualityCheckResultsReporter;
import org.tindalos.principle.domain.resultprocessing.reporter.Printer;
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdChecker;

public class Application {
	
	private final DesignQualityCheckService designQualityCheckService;
	private final DesignQualityCheckResultsReporter designQualityCheckResultsReporter;
	private final ThresholdChecker thresholdChecker;
	
	public Application(DesignQualityCheckService designQualityCheckService, DesignQualityCheckResultsReporter designQualityCheckResultsReporter, ThresholdChecker thresholdChecker) {
		this.designQualityCheckService = designQualityCheckService;
		this.designQualityCheckResultsReporter = designQualityCheckResultsReporter;
		this.thresholdChecker = thresholdChecker;
	}

	public void doIt(DesignQualityCheckParameters parameters, Printer printer) {
        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(parameters);
        designQualityCheckResultsReporter.report(checkResults, printer);
        thresholdChecker.trespassed(checkResults, parameters.getChecks());
	}

}
