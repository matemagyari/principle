package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.coredetector.DesignQualityCheckParameters;

public class DesignQualityCheckService {

    private final PackageAnalyzer packageAnalyzer;
    private final DesignQualityDetectorsRunner designQualityDetectorsRunner;

    public DesignQualityCheckService(PackageAnalyzer packageAnalyzer, DesignQualityDetectorsRunner designQualityDetectorsRunner) {
        this.packageAnalyzer = packageAnalyzer;
        this.designQualityDetectorsRunner = designQualityDetectorsRunner;
    }

    public DesignQualityCheckResults analyze(DesignQualityCheckParameters parameters) {
        List<Package> packages = packageAnalyzer.analyze(parameters);
        return designQualityDetectorsRunner.execute(packages, parameters);
    }

}
