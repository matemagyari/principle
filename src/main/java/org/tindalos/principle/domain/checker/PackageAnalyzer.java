package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;

public interface PackageAnalyzer {
    
    List<Package> analyze(DesignQualityCheckConfiguration parameters);

}
