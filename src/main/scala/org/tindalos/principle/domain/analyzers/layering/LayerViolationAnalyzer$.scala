package org.tindalos.principle.domain.analyzers.layering

import org.tindalos.principle.domain.AnalysisResult
import org.tindalos.principle.domain.agentscore.{Analyzer, AnalysisInput}
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.core.Package

import scala.collection.JavaConverters.asScalaBufferConverter

case class LayerReference(referrer:String, referee:String)

case class LayerViolationsResult(
    violations: List[LayerReference],
    threshold: Int) extends AnalysisResult {

  override def constraintViolated() = violations.length > threshold
}

object LayerViolationAnalyzer extends Analyzer {

  override def analyze(checkInput: AnalysisInput) = {
    val layerReferences = findViolations(checkInput.packages, checkInput.analysisPlan)
    new LayerViolationsResult(layerReferences, checkInput.layeringExpectations().violationThreshold)
  }

  override def isEnabled(expectations: Constraints) = expectations.layering != null

  private def findViolations(packages: List[Package], configuration: AnalysisPlan): List[LayerReference] = {

    val layers = configuration.expectations.layering.layers.asScala.map(configuration.basePackage + "." + _).toList

    for (aPackage <- packages
            if aPackage.reference.startsWith(configuration.basePackage);
            layer = layers.find(aPackage.reference.startsWith(_))
            if layer.isDefined;
         referencedPackage <- aPackage.getOwnPackageReferences()
            if referencedPackage.startsWith(configuration.basePackage);
         referencedLayer <- layers.slice(0, layers.indexOf(layer.get))
            if referencedPackage.startsWith(referencedLayer)
    ) yield LayerReference(aPackage.reference.name, referencedPackage.name)
  }

}