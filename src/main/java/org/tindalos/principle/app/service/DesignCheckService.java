package org.tindalos.principle.app.service;

import java.util.List;

import org.tindalos.principle.domain.checker.DesignCheckResults;
import org.tindalos.principle.domain.checker.DesignChecker;
import org.tindalos.principle.domain.checker.PackageAnalyzer;
import org.tindalos.principle.domain.core.DesingCheckerParameters;
import org.tindalos.principle.domain.core.Package;

public class DesignCheckService {

    private final PackageAnalyzer packageAnalyzer;
    private final DesignChecker designChecker;

    public DesignCheckService(PackageAnalyzer packageAnalyzer, DesignChecker designChecker) {
        this.packageAnalyzer = packageAnalyzer;
        this.designChecker = designChecker;
    }

    public DesignCheckResults analyze(DesingCheckerParameters parameters) {
        List<Package> packages = packageAnalyzer.analyze(parameters);
        return designChecker.execute(packages, parameters);
    }

}
