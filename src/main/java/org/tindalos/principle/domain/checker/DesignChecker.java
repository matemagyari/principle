package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.DesignCheckerParameters;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.detector.core.CheckInput;
import org.tindalos.principle.domain.detector.core.CheckResult;
import org.tindalos.principle.domain.detector.core.Detector;

import com.google.common.collect.Lists;

public class DesignChecker {

    private final List<Detector<? extends CheckResult>> detectors;

    public DesignChecker(List<Detector<? extends CheckResult>> detectors) {
        this.detectors = Lists.newArrayList(detectors);
    }
    public DesignChecker(Detector<? extends CheckResult>... detectors) {
        this.detectors = Lists.newArrayList(detectors);
    }

    public DesignCheckResults execute(List<Package> packages, DesignCheckerParameters parameters) {

        List<CheckResult> checkResults = Lists.newArrayList();
        CheckInput checkInput = new CheckInput(packages, parameters);
        for (Detector<? extends CheckResult> detector : detectors) {
            CheckResult checkResult = detector.analyze(checkInput);
            checkResults.add(checkResult);
        }
        return new DesignCheckResults(checkResults);
    }

}
