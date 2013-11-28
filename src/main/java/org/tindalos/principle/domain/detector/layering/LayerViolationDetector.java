package org.tindalos.principle.domain.detector.layering;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.coredetector.CheckInput;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class LayerViolationDetector implements Detector {

    public LayerViolationsResult analyze(CheckInput checkInput) {
        List<LayerReference> layerReferences = findViolations(checkInput.getPackages(), checkInput.getConfiguration());
        return new LayerViolationsResult(layerReferences, checkInput.getLayeringExpectations());
    }

    public boolean isWanted(DesignQualityExpectations expectations) {
        return expectations.getLayering() != null;
    }

    private List<LayerReference> findViolations(List<Package> packages, DesignQualityCheckConfiguration configuration) {

        List<LayerReference> violations = Lists.newArrayList();

        for (Package aPackage : filterToRelevantPackages(packages, configuration.getBasePackage())) {
            List<String> layers = layers(configuration);
            Optional<String> layer = getLayer(aPackage, layers, configuration.getBasePackage());
            if (layer.isPresent()) {
                List<String> outerLayers = outerLayers(layers, layer.get());
                violations.addAll(getReferencesToLayers(aPackage, outerLayers, configuration.getBasePackage()));
            }
        }

        return violations;
    }

    private List<String> layers(final DesignQualityCheckConfiguration configuration) {
        Function<String, String> function = new Function<String, String>() {
            public String apply(String input) {
                return configuration.getBasePackage() + "." + input;
            }
        };
        return Lists.newArrayList(Iterables.transform(configuration.getExpectations().getLayering().getLayers(),
                function));
    }

    private static List<String> outerLayers(List<String> layers, String layer) {
        return layers.subList(0, layers.indexOf(layer));
    }

    private static Optional<String> getLayer(Package aPackage, List<String> layers, String basePackageName) {
        for (String layer : layers) {
            if (aPackage.getReference().startsWith(layer)) {
                return Optional.of(layer);
            }
        }
        return Optional.absent();
    }

    private List<LayerReference> getReferencesToLayers(Package aPackage, List<String> layers, String basePackage) {
        List<LayerReference> references = Lists.newArrayList();

        Set<PackageReference> allReferencedPackages = aPackage.getOwnPackageReferences();

        List<PackageReference> referencedPackages = filterToRelevantPackageReferences(allReferencedPackages,
                basePackage);

        for (PackageReference referencedPackage : referencedPackages) {
            for (String layer : layers) {
                if (referencedPackage.startsWith(layer)) {
                    references.add(new LayerReference(aPackage.getReference().getName(), referencedPackage.getName()));
                }
            }
        }
        return references;
    }

    private List<PackageReference> filterToRelevantPackageReferences(Set<PackageReference> packages,
            final String basePackage) {

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
