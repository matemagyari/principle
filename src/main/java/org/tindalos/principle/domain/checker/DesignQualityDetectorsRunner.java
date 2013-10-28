package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.core.DesignQualityCheckParameters;
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

    public DesignQualityCheckResults execute(List<Package> packages, DesignQualityCheckParameters parameters) {

        List<CheckResult> checkResults = Lists.newArrayList();
        CheckInput checkInput = new CheckInput(packages, parameters);
        for (Detector detector : detectors) {
            CheckResult checkResult = detector.analyze(checkInput);
            checkResults.add(checkResult);
        }
        return new DesignQualityCheckResults(checkResults);
    }

}
