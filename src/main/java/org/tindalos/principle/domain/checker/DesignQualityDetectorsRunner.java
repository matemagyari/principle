package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.logging.TheLogger;
import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;

import com.google.common.collect.Lists;

public class DesignQualityDetectorsRunner {

	private final List<Detector> detectors;

	public DesignQualityDetectorsRunner(List<Detector> detectors) {
		this.detectors = Lists.newArrayList(detectors);
	}

	public DesignQualityDetectorsRunner(Detector... detectors) {
		this.detectors = Lists.newArrayList(detectors);
	}

	public DesignQualityCheckResults execute(List<Package> packages, DesignQualityCheckConfiguration designQualityCheckConfiguration) {

		List<CheckResult> checkResults = Lists.newArrayList();
		CheckInput checkInput = new CheckInput(packages, designQualityCheckConfiguration);
		for (Detector detector : detectors) {
			if (detector.isWanted(designQualityCheckConfiguration.getExpectations())) {
			    TheLogger.info(detector + " is running");
				CheckResult checkResult = detector.analyze(checkInput);
				checkResults.add(checkResult);
			}
		}
		return new DesignQualityCheckResults(checkResults);
	}

}
