package org.tindalos.principle.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.List;

import jdepend.framework.JavaPackage;

import org.tindalos.principle.domain.core.Package;

import com.google.common.collect.Lists;


public class PackageBuilder {
    
    private final PackageTransformer packageTransformer;
    
    public PackageBuilder(PackageTransformer packageTransformer) {
        this.packageTransformer = packageTransformer;
    }

    public List<Package> build(Collection<JavaPackage> analyzedPackages, String basePackage) {
        
        List<Package> result = Lists.newArrayList();
        for (JavaPackage javaPackage : analyzedPackages) {
            Package aPackage = packageTransformer.transform(basePackage, javaPackage);
            result.add(aPackage);
        }
        return result;
    }

}
