package org.tindalos.principle.app.service;

import java.util.List;

import org.tindalos.principle.domain.checker.DesignQualityCheckResults;
import org.tindalos.principle.domain.checker.DesignQualityChecker;
import org.tindalos.principle.domain.checker.PackageAnalyzer;
import org.tindalos.principle.domain.core.DesignQualityCheckParameters;
import org.tindalos.principle.domain.core.Package;

public class DesignQualityCheckService {

    private final PackageAnalyzer packageAnalyzer;
    private final DesignQualityChecker designQualityChecker;

    public DesignQualityCheckService(PackageAnalyzer packageAnalyzer, DesignQualityChecker designQualityChecker) {
        this.packageAnalyzer = packageAnalyzer;
        this.designQualityChecker = designQualityChecker;
    }

    public DesignQualityCheckResults analyze(DesignQualityCheckParameters parameters) {
        List<Package> packages = packageAnalyzer.analyze(parameters);
        return designQualityChecker.execute(packages, parameters);
    }

}
