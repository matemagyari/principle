package org.principle.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.List;

import jdepend.framework.JavaPackage;


import com.google.common.collect.Lists;


public class PackageBuilder {

    public List<org.principle.domain.detector.cycledetector.core.Package> build(Collection<JavaPackage> analyzedPackages, String basePackage) {
        
        List<org.principle.domain.detector.cycledetector.core.Package> result = Lists.newArrayList();
        for (JavaPackage javaPackage : analyzedPackages) {
            org.principle.domain.detector.cycledetector.core.Package aPackage = transform(basePackage, javaPackage);
            result.add(aPackage);
        }
        return result;
    }

    private org.principle.domain.detector.cycledetector.core.Package transform(String basePackage,
            JavaPackage javaPackage) {
        return new LazyLoadingJDependBasedPackage(javaPackage, basePackage);
    }

}
