package org.tindalos.principle.infrastructure.service.jdepend;

import jdepend.framework.JavaPackage;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.packages.PackageMetrics;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Creates domain packages from JDepend package models for a configured root package.
 */
public final class PackageFactory {

    private final String rootPackage;

    public PackageFactory(String rootPackage) {
        this.rootPackage = Objects.requireNonNull(rootPackage, "rootPackage");
    }

    public boolean isRelevant(JavaPackage javaPackage) {
        return javaPackage.getName().startsWith(rootPackage);
    }

    public Package transform(JavaPackage javaPackage) {
        var metrics = calculateMetrics(javaPackage);
        Predicate<JavaPackage> relevanceCheck = this::isRelevant;
        return new LazyLoadingJDependBasedPackage(javaPackage, metrics, this, relevanceCheck);
    }

    public Function<List<JavaPackage>, List<Package>> buildPackageListFactory(UnaryOperator<List<Package>> sortByName) {
        Objects.requireNonNull(sortByName, "sortByName");
        return analyzedPackages -> sortByName.apply(
                analyzedPackages.stream()
                        .filter(this::isRelevant)
                        .map(this::transform)
                        .toList());
    }

    private PackageMetrics calculateMetrics(JavaPackage javaPackage) {
        return new PackageMetrics(
                javaPackage.afferentCoupling(),
                javaPackage.efferentCoupling(),
                javaPackage.abstractness(),
                javaPackage.instability(),
                javaPackage.distance());
    }
}