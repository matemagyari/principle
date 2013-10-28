package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.DesignQualityCheckParameters;

public interface PackageAnalyzer {
    
    List<Package> analyze(DesignQualityCheckParameters parameters);

}
