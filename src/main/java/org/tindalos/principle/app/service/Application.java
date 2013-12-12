package org.tindalos.principle.app.service;

import org.tindalos.principle.domain.checker.DesignQualityCheckResults;
import org.tindalos.principle.domain.checker.DesignQualityCheckService;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.resultprocessing.reporter.DesignQualityCheckResultsReporter;
import org.tindalos.principle.domain.resultprocessing.reporter.Printer;
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException;

public class Application {
	
	private final DesignQualityCheckService designQualityCheckService;
	private final DesignQualityCheckResultsReporter designQualityCheckResultsReporter;
	
	public Application(DesignQualityCheckService designQualityCheckService, DesignQualityCheckResultsReporter designQualityCheckResultsReporter) {
		this.designQualityCheckService = designQualityCheckService;
		this.designQualityCheckResultsReporter = designQualityCheckResultsReporter;
	}

	public DesignQualityCheckResults doIt(DesignQualityCheckConfiguration designQualityCheckConfiguration, Printer printer) {
        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(designQualityCheckConfiguration);
        designQualityCheckResultsReporter.report(checkResults, printer);
        
        if (checkResults.failExpectations()) {
        	throw new ThresholdTrespassedException();
        }
        return checkResults;
	}

}
