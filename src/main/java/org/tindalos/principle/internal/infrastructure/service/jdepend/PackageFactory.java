package org.tindalos.principle.internal.infrastructure.service.jdepend;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.tindalos.principle.internal.domain.core.Package;
import org.tindalos.principle.internal.domain.core.packages.PackageMetrics;

import jdepend.framework.JavaPackage;

/**
 * Creates domain packages from JDepend package models for a configured root package.
 */
final class PackageFactory {

    private final String rootPackage;

    PackageFactory(String rootPackage) {
        this.rootPackage = Objects.requireNonNull(rootPackage, "rootPackage");
    }

    boolean isRelevant(JavaPackage javaPackage) {
        return javaPackage.getName().startsWith(rootPackage);
    }

    Package transform(JavaPackage javaPackage) {
        var metrics = calculateMetrics(javaPackage);
        Predicate<JavaPackage> relevanceCheck = this::isRelevant;
        return new LazyLoadingJDependBasedPackage(javaPackage, metrics, this, relevanceCheck);
    }

    Function<List<JavaPackage>, List<Package>> buildPackageListFactory(UnaryOperator<List<Package>> sortByName) {
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