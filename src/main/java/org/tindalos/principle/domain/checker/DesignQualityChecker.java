package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.DesignQualityCheckParameters;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.detector.core.CheckInput;
import org.tindalos.principle.domain.detector.core.CheckResult;
import org.tindalos.principle.domain.detector.core.Detector;

import com.google.common.collect.Lists;

public class DesignQualityChecker {

    private final List<Detector> detectors;

    public DesignQualityChecker(List<Detector> detectors) {
        this.detectors = Lists.newArrayList(detectors);
    }
    public DesignQualityChecker(Detector... detectors) {
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
