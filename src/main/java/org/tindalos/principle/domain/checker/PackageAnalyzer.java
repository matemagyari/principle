package org.tindalos.principle.domain.checker;

import java.util.List;

import org.tindalos.principle.domain.core.DesignCheckerParameters;
import org.tindalos.principle.domain.core.Package;

public interface PackageAnalyzer {
    
    List<Package> analyze(DesignCheckerParameters parameters);

}
