package org.tindalos.principle.domain.core;

import java.util.List;

import com.google.common.collect.Lists;

public class CheckInput {
    
    private final List<Package> packages;
    private final DesingCheckerParameters parameters;
    
    public CheckInput(List<Package> packages, DesingCheckerParameters parameters) {
        this.packages = Lists.newArrayList(packages);
        this.parameters = parameters;
    }
    
    public List<Package> getPackages() {
        return packages;
    }
    public DesingCheckerParameters getParameters() {
        return parameters;
    }
    

}
