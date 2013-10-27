package org.tindalos.principle.app.service;

import org.tindalos.principle.domain.checker.DesignQualityCheckResults;
import org.tindalos.principle.domain.checker.DesignQualityCheckService;
import org.tindalos.principle.domain.checkerparameter.Checks;
import org.tindalos.principle.domain.coredetector.DesignQualityCheckParameters;
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

	public void doIt(Checks checks, String basePackage, Printer printer) {
        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(buildParameters(checks, basePackage));
        designQualityCheckResultsReporter.report(checkResults, printer);
        thresholdChecker.trespassed(checkResults, checks);
	}
	
    private DesignQualityCheckParameters buildParameters(Checks checks, String basePackage) {
        DesignQualityCheckParameters parameters = new DesignQualityCheckParameters(basePackage, checks.getLayering().getLayers());
        parameters.setMaxSAPDistance(checks.getPackageCoupling().getSAP().getMaxDistance());
        return parameters;
    }

}
