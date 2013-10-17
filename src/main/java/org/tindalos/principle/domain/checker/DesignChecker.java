package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.CheckInput;
import org.tindalos.principle.domain.core.DesingCheckerParameters;
import org.tindalos.principle.domain.core.Detector;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.reporting.CheckResult;

import com.google.common.collect.Lists;

public class DesignChecker {

    private final List<Detector> detectors;

    public DesignChecker(List<Detector> detectors) {
        this.detectors = Lists.newArrayList(detectors);
    }
    public DesignChecker(Detector... detectors) {
        this.detectors = Lists.newArrayList(detectors);
    }

    public DesignCheckResults execute(List<Package> packages, DesingCheckerParameters parameters) {

        List<CheckResult> checkResults = Lists.newArrayList();
        CheckInput checkInput = new CheckInput(packages, parameters);
        for (Detector detector : detectors) {
            CheckResult checkResult = detector.analyze(checkInput);
            checkResults.add(checkResult);
        }
        return new DesignCheckResults(checkResults);
    }

}
