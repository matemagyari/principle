package org.tindalos.principle.domain.detector.submoduleboundaries;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class Submodule {

    private final SubmoduleId id;
    private final Set<SubmoduleId> plannedDependencies;
    private final Set<Package> packages;
    private final Map<PackageReference, Package> packageReferences;
    private final Set<PackageReference> outgoingReferences;

    public Submodule(SubmoduleId id, Set<Package> packages, Map<PackageReference, Package> packageReferences, Set<SubmoduleId> dependencies) {
        this.id = id;
        this.plannedDependencies = Sets.newHashSet(dependencies);
        this.packages = Sets.newHashSet(packages);
        this.packageReferences =packageReferences;
        this.outgoingReferences = collectOutgoingDependenies();
    }
    
    private Set<PackageReference> collectOutgoingDependenies() {
        Set<PackageReference> allOutgoingReferences = Sets.newHashSet();
        for (Package aPackage : packages) {
            Set<PackageReference> outgoingReferences = aPackage.cumulatedDependencies(packageReferences);
            allOutgoingReferences.addAll(outgoingReferences);
        }
        return allOutgoingReferences;
    }

    public Set<Submodule> findOnvalidDependencies(List<Submodule> actualDependencies) {
        Predicate<Submodule> predicate = new Predicate<Submodule>() {
            public boolean apply(Submodule submodule) {
                return submodule.isAffectedBy(outgoingReferences) && plannedDependencies.contains(submodule.getId());
            }
        };
        return Sets.newHashSet(Iterables.filter(actualDependencies, predicate));
    }

    private boolean isAffectedBy(Set<PackageReference> allOutgoingReferences) {
        for (PackageReference packageReference : allOutgoingReferences) {
            for (Package aPackage : packages) {
                if (packageReference.pointsInside(aPackage.getReference())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Set<SubmoduleId> getPlannedDependencies() {
        return Sets.newHashSet(plannedDependencies);
    }
    
    private SubmoduleId getId() {
        return id;
    }

}
