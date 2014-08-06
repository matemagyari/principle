package org.tindalos.principle.domain.detector.layering

import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.expectations.DesignQualityExpectations

class LayerViolationDetector extends Detector {

  override def analyze(checkInput: CheckInput) = {
    val layerReferences = findViolations(checkInput.packages, checkInput.designQualityCheckConfiguration)
    new LayerViolationsResult(layerReferences, checkInput.layeringExpectations())
  }

  override def isWanted(expectations: DesignQualityExpectations) = expectations.layering != null

  private def findViolations(packages: List[Package], configuration: DesignQualityCheckConfiguration): List[LayerReference] = {

    val layers = configuration.layeringExpectations().layers.map(configuration.basePackage + "." + _).toList

    for (aPackage <- packages
            if aPackage.reference.startsWith(configuration.basePackage);
            layer = layers.find(aPackage.reference.startsWith(_))
            if layer.isDefined;
         referencedPackage <- aPackage.getOwnPackageReferences()
            if referencedPackage.startsWith(configuration.basePackage);
         referencedLayer <- layers.slice(0, layers.indexOf(layer.get))
            if referencedPackage.startsWith(referencedLayer)
    ) yield new LayerReference(aPackage.reference.name, referencedPackage.name)
  }

}