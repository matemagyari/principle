package org.tindalos.principle.domain.detector.core;

import java.util.List;

import org.tindalos.principle.domain.core.DesignCheckerParameters;
import org.tindalos.principle.domain.core.Package;

import com.google.common.collect.Lists;

public class CheckInput {
    
    private final List<Package> packages;
    private final DesignCheckerParameters parameters;
    
    public CheckInput(List<Package> packages, DesignCheckerParameters parameters) {
        this.packages = Lists.newArrayList(packages);
        this.parameters = parameters;
    }
    
    public List<Package> getPackages() {
        return packages;
    }
    public DesignCheckerParameters getParameters() {
        return parameters;
    }
    

}
