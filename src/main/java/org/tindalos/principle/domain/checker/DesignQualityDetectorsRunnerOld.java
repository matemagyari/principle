package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.logging.TheLogger;
import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;

import com.google.common.collect.Lists;

public class DesignQualityDetectorsRunnerOld {

	private final List<Detector> detectors;

	public DesignQualityDetectorsRunnerOld(List<Detector> detectors) {
		this.detectors = Lists.newArrayList(detectors);
	}

	public DesignQualityDetectorsRunnerOld(Detector... detectors) {
		this.detectors = Lists.newArrayList(detectors);
	}

	public DesignQualityCheckResults execute(List<Package> packages, DesignQualityCheckConfiguration designQualityCheckConfiguration) {

		List<CheckResult> checkResults = Lists.newArrayList();
		CheckInput checkInput = new CheckInput(packages, designQualityCheckConfiguration);
		for (Detector detector : detectors) {
			if (detector.isWanted(designQualityCheckConfiguration.getExpectations())) {
				runDetector(checkResults, checkInput, detector);
			}
		}
		return new DesignQualityCheckResults(checkResults);
	}

	private void runDetector(List<CheckResult> checkResults, CheckInput checkInput, Detector detector) {
		try {
			TheLogger.info(detector + " is running");
			CheckResult checkResult = detector.analyze(checkInput);
			checkResults.add(checkResult);
		} catch (RuntimeException unwantedException) {
			TheLogger.error(unwantedException.getMessage());
		}
	}

}
