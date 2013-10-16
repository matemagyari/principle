package org.principle.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.List;

import jdepend.framework.JavaPackage;

import org.principle.domain.detector.cycledetector.core.Package;

import com.google.common.collect.Lists;


public class PackageBuilder {

    public List<Package> build(Collection<JavaPackage> analyzedPackages, String basePackage) {
        
        List<Package> result = Lists.newArrayList();
        for (JavaPackage javaPackage : analyzedPackages) {
            Package aPackage = transform(basePackage, javaPackage);
            result.add(aPackage);
        }
        return result;
    }

    private Package transform(String basePackage,
            JavaPackage javaPackage) {
        return new LazyLoadingJDependBasedPackage(javaPackage, basePackage);
    }

}
