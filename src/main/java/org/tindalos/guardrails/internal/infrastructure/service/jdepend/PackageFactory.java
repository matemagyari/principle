package org.tindalos.guardrails.internal.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;

import jdepend.framework.JavaPackage;

/**
 * Creates domain packages from JDepend package models for a configured root package.
 */
final class PackageFactory {

    private static final Set<String> VALID_EXTERNAL_EFFERENTS = Set.of("java", "scala");

    private final String rootPackage;

    PackageFactory(String rootPackage) {
        this.rootPackage = Objects.requireNonNull(rootPackage, "rootPackage");
    }

    boolean isRelevant(JavaPackage javaPackage) {
        return javaPackage.getName().startsWith(rootPackage);
    }

    private boolean isNotValidExternalEfferent(JavaPackage aPackage) {
        return VALID_EXTERNAL_EFFERENTS.stream().noneMatch(prefix -> aPackage.getName().startsWith(prefix));
    }

    @SuppressWarnings("unchecked")
    LazyLoadingJDependBasedPackage transform(JavaPackage javaPackage) {
        var metrics = calculateMetrics(javaPackage);
        var reference = new PackageReference(javaPackage.getName());
        var isUnreferred = metrics.afferentCoupling() == 0;

        Collection<JavaPackage> efferents = (Collection<JavaPackage>) javaPackage.getEfferents();

        Set<PackageReference> ownReferences = efferents.stream()
                .filter(this::isRelevant)
                .map(eff -> new PackageReference(eff.getName()))
                .collect(Collectors.toUnmodifiableSet());

        Set<PackageReference> ownExternalReferences = efferents.stream()
                .filter(eff -> !isRelevant(eff) && isNotValidExternalEfferent(eff))
                .map(eff -> new PackageReference(eff.getName()))
                .collect(Collectors.toUnmodifiableSet());

        return new LazyLoadingJDependBasedPackage(reference, metrics, ownReferences, ownExternalReferences, isUnreferred);
    }

    private Package toDomainPackage(LazyLoadingJDependBasedPackage record) {
        return new Package(
                record.reference(),
                record.metrics(),
                record.ownPackageReferences(),
                record.ownExternalPackageReferences(),
                record.isUnreferred()
        );
    }

    Function<List<JavaPackage>, List<Package>> buildPackageListFactory(UnaryOperator<List<Package>> sortByName) {
        Objects.requireNonNull(sortByName, "sortByName");
        return analyzedPackages -> sortByName.apply(
                analyzedPackages.stream()
                        .filter(this::isRelevant)
                        .map(this::transform)
                        .map(this::toDomainPackage)
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