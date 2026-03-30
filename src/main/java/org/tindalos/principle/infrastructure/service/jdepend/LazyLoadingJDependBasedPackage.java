package org.tindalos.principle.infrastructure.service.jdepend;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.packages.PackageMetrics;
import org.tindalos.principle.domain.core.packages.PackageReference;

import jdepend.framework.JavaPackage;

/**
 * Package adapter backed by a JDepend package.
 * Resolves internal and external references lazily from the underlying JDepend model.
 */
public final class LazyLoadingJDependBasedPackage extends Package {

    private static final Set<String> VALID_EXTERNAL_EFFERENTS = Set.of("java", "scala");

    private final JavaPackage javaPackage;
    private final PackageMetrics metrics;
    private final PackageFactory packageFactory;
    private final Predicate<JavaPackage> isRelevant;

    public LazyLoadingJDependBasedPackage(
            JavaPackage javaPackage,
            PackageMetrics metrics,
            PackageFactory packageFactory,
            Predicate<JavaPackage> isRelevant) {
        super(Objects.requireNonNull(javaPackage, "javaPackage").getName());
        this.javaPackage = javaPackage;
        this.metrics = Objects.requireNonNull(metrics, "metrics");
        this.packageFactory = Objects.requireNonNull(packageFactory, "packageFactory");
        this.isRelevant = Objects.requireNonNull(isRelevant, "isRelevant");
    }

    @Override
    public PackageMetrics getMetrics() {
        return metrics;
    }

    @Override
    public boolean isUnreferred() {
        return metrics.afferentCoupling() == 0;
    }

    @Override
    public Set<PackageReference> getOwnPackageReferences() {
        return efferents().stream()
                .filter(isRelevant)
                .map(packageFactory::transform)
                .map(Package::reference)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<PackageReference> getOwnExternalPackageReferences() {
        return efferents().stream()
                .filter(aPackage -> !isRelevant.test(aPackage) && isNotValidExternalEfferent(aPackage))
                .map(packageFactory::transform)
                .map(Package::reference)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Collection<JavaPackage> efferents() {
        return (Collection<JavaPackage>) javaPackage.getEfferents();
    }

    private boolean isNotValidExternalEfferent(JavaPackage aPackage) {
        return VALID_EXTERNAL_EFFERENTS.stream().noneMatch(prefix -> aPackage.getName().startsWith(prefix));
    }
}