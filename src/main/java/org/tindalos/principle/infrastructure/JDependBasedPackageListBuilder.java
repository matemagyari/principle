package org.tindalos.principle.infrastructure;

import jdepend.framework.JavaPackage;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageSorterModule;
import org.tindalos.principle.domain.core.packages.PackageMetrics;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.infrastructure.service.jdepend.JDependRunner;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class JDependBasedPackageListBuilder implements PackageListBuilder {

    private final String rootPackage;

    public JDependBasedPackageListBuilder(String rootPackage) {
        this.rootPackage = rootPackage;
    }

    @Override
    public List<Package> build() {
        List<JavaPackage> analyzedPackages = JDependRunner.preparePackages(rootPackage, true);

        List<Package> unsorted = analyzedPackages.stream()
            .filter(this::isRelevant)
            .map(this::transform)
            .toList();

        return PackageSorterModule.sortByName(unsorted);
    }

    private boolean isRelevant(JavaPackage javaPackage) {
        return javaPackage.getName().startsWith(rootPackage);
    }

    private Package transform(JavaPackage javaPackage) {
        PackageMetrics metrics = new PackageMetrics(
            javaPackage.afferentCoupling(),
            javaPackage.efferentCoupling(),
            javaPackage.abstractness(),
            javaPackage.instability(),
            javaPackage.distance());

        return new LazyLoadingJDependBasedPackage(javaPackage, metrics);
    }

    private final class LazyLoadingJDependBasedPackage extends Package {

        private final JavaPackage javaPackage;
        private final PackageMetrics metrics;
        private final Set<String> validExternalEfferents = Set.of("java", "scala");

        private LazyLoadingJDependBasedPackage(JavaPackage javaPackage, PackageMetrics metrics) {
            super(javaPackage.getName());
            this.javaPackage = javaPackage;
            this.metrics = metrics;
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
                .filter(JDependBasedPackageListBuilder.this::isRelevant)
                .map(p -> new PackageReference(p.getName()))
                .collect(Collectors.toUnmodifiableSet());
        }

        @Override
        public Set<PackageReference> getOwnExternalPackageReferences() {
            return efferents().stream()
                .filter(p -> !JDependBasedPackageListBuilder.this.isRelevant(p) && isNotValidExternalEfferent(p))
                .map(p -> new PackageReference(p.getName()))
                .collect(Collectors.toUnmodifiableSet());
        }

        @SuppressWarnings("unchecked")
        private Collection<JavaPackage> efferents() {
            return (Collection<JavaPackage>) javaPackage.getEfferents();
        }

        private boolean isNotValidExternalEfferent(JavaPackage aPackage) {
            return validExternalEfferents.stream().noneMatch(prefix -> aPackage.getName().startsWith(prefix));
        }
    }
}
