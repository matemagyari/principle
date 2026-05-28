package org.tindalos.guardrails.internal.infrastructure.packages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuildingException;
import org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;

/**
 * Mutable representation of a package used during the tree-building process.
 * Decoupled from JDepend and other analytical frameworks.
 */
public class MutablePackage {

    private final PackageReference reference;
    private final List<MutablePackage> subPackages = new ArrayList<>();
    private final PackageMetrics metrics;
    private final Set<PackageReference> ownPackageReferences;
    private final Set<PackageReference> ownExternalPackageReferences;
    private final boolean unreferred;

    public MutablePackage(PackageReference reference) {
        this.reference = reference;
        this.metrics = PackageMetrics.UNDEFINED;
        this.ownPackageReferences = Collections.emptySet();
        this.ownExternalPackageReferences = Collections.emptySet();
        this.unreferred = true;
    }

    public MutablePackage(
            PackageReference reference,
            PackageMetrics metrics,
            Set<PackageReference> ownPackageReferences,
            Set<PackageReference> ownExternalPackageReferences,
            boolean unreferred) {
        this.reference = reference;
        this.metrics = metrics;
        this.ownPackageReferences = Set.copyOf(ownPackageReferences);
        this.ownExternalPackageReferences = Set.copyOf(ownExternalPackageReferences);
        this.unreferred = unreferred;
    }

    public PackageReference reference() {
        return reference;
    }

    public List<MutablePackage> subPackages() {
        return Collections.unmodifiableList(subPackages);
    }

    public void insert(MutablePackage aPackage) {
        if (this.equals(aPackage)) {
            throw new PackageStructureBuildingException("Attempted to insert into itself " + this);
        } else if (doesNotContain(aPackage)) {
            throw new PackageStructureBuildingException("Attempted to insert " + aPackage + " into " + this);
        } else if (isDirectSuperPackageOf(aPackage)) {
            subPackages.add(aPackage);
        } else {
            insertIndirectSubPackage(aPackage);
        }
    }

    private boolean isDirectSuperPackageOf(MutablePackage aPackage) {
        return reference.isDirectParentOf(aPackage.reference);
    }

    private boolean doesNotContain(MutablePackage aPackage) {
        return !aPackage.reference.pointsInside(reference);
    }

    private String firstPartOfRelativeNameTo(MutablePackage parentPackage) {
        return reference.firstPartOfRelativeNameTo(parentPackage.reference);
    }

    private void insertIndirectSubPackage(MutablePackage aPackage) {
        String relativeNameOfDirectSubPackage = aPackage.firstPartOfRelativeNameTo(this);
        getSubPackageByRelativeName(relativeNameOfDirectSubPackage).insert(aPackage);
    }

    private MutablePackage getSubPackageByRelativeName(String relativeName) {
        PackageReference targetReference = reference.child(relativeName);

        return subPackages.stream()
            .filter(subPackage -> subPackage.reference.equals(targetReference))
            .findFirst()
            .orElseGet(() -> {
                MutablePackage directSubPackage = new MutablePackage(reference.child(relativeName));
                subPackages.add(directSubPackage);
                return directSubPackage;
            });
    }

    /**
     * Converts this mutable package hierarchy into an immutable Package record.
     *
     * @return the immutable Package representation
     */
    public Package toImmutable() {
        List<Package> immutableSubPackages = subPackages.stream()
            .map(MutablePackage::toImmutable)
            .toList();
        return new Package(
            reference,
            metrics,
            ownPackageReferences,
            ownExternalPackageReferences,
            unreferred,
            immutableSubPackages
        );
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MutablePackage castOther && castOther.reference.equals(reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }

    @Override
    public String toString() {
        return reference.toString();
    }
}
