package org.tindalos.principle.domain.detector.layering

import org.tindalos.principle.domain.coredetector.{AnalysisResult, PackagesAndExpectations, Detector}
import org.tindalos.principle.domain.core.AnalysisInput
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.expectations.Expectations

case class LayerReference(referrer:String, referee:String)

class LayerViolationsResult(val violations: List[LayerReference], val threshold: Int) extends AnalysisResult {
  override def expectationsFailed() = violations.length > threshold
}

object LayerViolationDetector extends Detector {

  override def analyze(checkInput: PackagesAndExpectations) = {
    val layerReferences = findViolations(checkInput.packages, checkInput.expectationsConfig)
    new LayerViolationsResult(layerReferences, checkInput.layeringExpectations().violationsThreshold)
  }

  override def isWanted(expectations: Expectations) = expectations.layering != null

  private def findViolations(packages: List[Package], configuration: AnalysisInput): List[LayerReference] = {

    val layers = configuration.expectations.layering.layers.map(configuration.basePackage + "." + _).toList

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