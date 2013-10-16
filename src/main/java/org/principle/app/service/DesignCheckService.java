package org.principle.app.service;

import java.util.List;

import org.principle.domain.checker.DesignCheckResults;
import org.principle.domain.checker.DesignChecker;
import org.principle.domain.core.DesingCheckerParameters;
import org.principle.domain.detector.cycledetector.core.Package;

public class DesignCheckService {

    private final PackageAnalyzer packageAnalyzer;
    private final DesignChecker designChecker;

    public DesignCheckService(PackageAnalyzer packageAnalyzer, DesignChecker designChecker) {
        this.packageAnalyzer = packageAnalyzer;
        this.designChecker = designChecker;
    }

    public DesignCheckResults analyze(DesingCheckerParameters parameters) {
        List<Package> packages = packageAnalyzer.analyze(parameters);
        return designChecker.execute(packages);
    }

}
