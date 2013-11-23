package org.tindalos.principle.domain.coredetector;

import java.util.List;

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.Layering;
import org.tindalos.principle.domain.expectations.PackageCoupling;
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint;

import com.google.common.collect.Lists;

public class CheckInput {
	
    private final List<Package> packages;
    private final DesignQualityCheckConfiguration designQualityCheckConfiguration;
    
    public CheckInput(List<Package> packages, DesignQualityCheckConfiguration designQualityCheckConfiguration) {
        this.packages = Lists.newArrayList(packages);
        this.designQualityCheckConfiguration = designQualityCheckConfiguration;
    }
    
    public String getBasePackage() {
        return designQualityCheckConfiguration.getBasePackage();
    }
    public List<Package> getPackages() {
        return Lists.newArrayList(packages);
    }
    public DesignQualityCheckConfiguration getConfiguration() {
        return designQualityCheckConfiguration;
    }
    
    public PackageCoupling getPackageCouplingExpectations() {
    	return getExpectations().getPackageCoupling();
    }
    
    public Layering getLayeringExpectations() {
    	return getExpectations().getLayering();
    }
    public SubmodulesBlueprint getSubmodulesBlueprint() {
        return getExpectations().getSubmodulesBlueprint();
    }
    
    private DesignQualityExpectations getExpectations() {
    	return this.designQualityCheckConfiguration.getExpectations();
    }
    

}
