package org.principle.domain.detector.layerviolationdetector;

import java.util.Collection;
import java.util.List;

import jdepend.framework.JavaPackage;

import org.principle.domain.core.DesingCheckerParameters;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class LayerViolationDetector {

    private final DesingCheckerParameters parameters;

    public LayerViolationDetector(DesingCheckerParameters parameters) {
        this.parameters = parameters;
    }

    public List<LayerReference> findViolations(Collection<JavaPackage> packages) {

        List<LayerReference> violations = Lists.newArrayList();

        for (JavaPackage aPackage : filterToRelevantPackages(packages)) {
            Optional<String> layer = getLayer(aPackage);
            if (layer.isPresent()) {
                List<String> outerLayers = parameters.getOuterLayers(layer.get());
                violations.addAll(getReferencesToLayers(aPackage, outerLayers));
            }
        }

        // for (JavaPackage aPackage : packages) {
        // if (inLayer(aPackage, parameters.getDomainPackage())) {
        // violations.addAll(getReferencesToLayers(aPackage,
        // parameters.getAppPackage(), parameters.getInfrastructurePackage()));
        // } else if (inLayer(aPackage, parameters.getAppPackage())) {
        // violations.addAll(getReferencesToLayers(aPackage,
        // parameters.getInfrastructurePackage()));
        // }
        // }
        return violations;
    }

    private Optional<String> getLayer(JavaPackage aPackage) {
        for (String layer : parameters.getLayers()) {
            if (aPackage.getName().startsWith(layer)) {
                return Optional.of(layer);
            }
        }
        return Optional.absent();
    }

    private static boolean inLayer(JavaPackage aPackage, String layer) {
        return aPackage.getName().startsWith(layer);
    }

    private List<LayerReference> getReferencesToLayers(JavaPackage aPackage, final String... layers) {
        return getReferencesToLayers(aPackage, Lists.newArrayList(layers));
    }

    @SuppressWarnings("unchecked")
    private List<LayerReference> getReferencesToLayers(JavaPackage aPackage, List<String> layers) {
        List<LayerReference> references = Lists.newArrayList();

        Collection<JavaPackage> allReferencedPackages = aPackage.getEfferents();

        Collection<JavaPackage> referencedPackages = filterToRelevantPackages(allReferencedPackages);

        for (JavaPackage referencedPackage : referencedPackages) {
            for (String layer : layers) {
                if (referencedPackage.getName().startsWith(layer)) {
                    references.add(new LayerReference(aPackage.getName(), referencedPackage.getName()));
                }
            }
        }
        return references;
    }

    private List<JavaPackage> filterToRelevantPackages(Collection<JavaPackage> packages) {

        Predicate<JavaPackage> filter = new Predicate<JavaPackage>() {

            public boolean apply(JavaPackage input) {
                return input.getName().startsWith(parameters.getBasePackage());
            }

        };
        return Lists.newArrayList(Iterables.filter(packages, filter));
    }

}
