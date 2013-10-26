package org.tindalos.principle.domain.detector.layering;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.tindalos.principle.domain.core.DesignQualityCheckParameters;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.detector.core.CheckInput;
import org.tindalos.principle.domain.detector.core.Detector;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class LayerViolationDetector implements Detector {
	
	public static final String ID = "LayerViolationDetector";
    
    public LayerViolationsResult analyze(CheckInput checkInput) {
        List<LayerReference> layerReferences = findViolations(checkInput.getPackages(), checkInput.getParameters());
        return new LayerViolationsResult(layerReferences);
    }

    private List<LayerReference> findViolations(List<Package> packages, DesignQualityCheckParameters parameters) {

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

    private Optional<String> getLayer(Package aPackage, DesignQualityCheckParameters parameters) {
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
