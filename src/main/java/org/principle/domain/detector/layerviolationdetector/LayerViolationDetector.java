package org.principle.domain.detector.layerviolationdetector;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.principle.domain.core.DesingCheckerParameters;
import org.principle.domain.core.Package;
import org.principle.domain.core.PackageReference;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class LayerViolationDetector {

    public List<LayerReference> findViolations(List<Package> packages, DesingCheckerParameters parameters) {

        List<LayerReference> violations = Lists.newArrayList();

        for (Package aPackage : filterToRelevantPackages(packages, parameters.getBasePackage())) {
            Optional<String> layer = getLayer(aPackage, parameters);
            if (layer.isPresent()) {
                List<String> outerLayers = parameters.getOuterLayers(layer.get());
                violations.addAll(getReferencesToLayers(aPackage, outerLayers, parameters.getBasePackage()));
            }
        }

        return violations;
    }

    private Optional<String> getLayer(Package aPackage, DesingCheckerParameters parameters) {
        for (String layer : parameters.getLayers()) {
            if (aPackage.getReference().startsWith(layer)) {
                return Optional.of(layer);
            }
        }
        return Optional.absent();
    }

    private List<LayerReference> getReferencesToLayers(Package aPackage, List<String> layers, String basePackage) {
        List<LayerReference> references = Lists.newArrayList();

        Set<PackageReference> allReferencedPackages = aPackage.getOwnPackageReferences();

        List<PackageReference> referencedPackages = filterToRelevantPackageReferences(allReferencedPackages, basePackage);

        for (PackageReference referencedPackage : referencedPackages) {
            for (String layer : layers) {
                if (referencedPackage.startsWith(layer)) {
                    references.add(new LayerReference(aPackage.getReference().getName(), referencedPackage.getName()));
                }
            }
        }
        return references;
    }

    private List<PackageReference> filterToRelevantPackageReferences(Set<PackageReference> packages, final String basePackage) {

        Predicate<PackageReference> filter = new Predicate<PackageReference>() {

            public boolean apply(PackageReference input) {
                return input.startsWith(basePackage);
            }

        };
        return Lists.newArrayList(Iterables.filter(packages, filter));
    }
    
    private List<Package> filterToRelevantPackages(Collection<Package> packages, final String basePackage) {
        
        Predicate<Package> filter = new Predicate<Package>() {
            
            public boolean apply(Package input) {
                return input.getReference().startsWith(basePackage);
            }
            
        };
        return Lists.newArrayList(Iterables.filter(packages, filter));
    }

}
