package org.tindalos.principle.domain.agents.layering

import org.tindalos.principle.domain.agentscore.{AnalysisResult, AnalysisInput, Agent}
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.expectations.Expectations

case class LayerReference(referrer:String, referee:String)

case class LayerViolationsResult(
    violations: List[LayerReference],
    threshold: Int) extends AnalysisResult {

  override def expectationsFailed() = violations.length > threshold
}

object LayerViolationAgent extends Agent {

  override def analyze(checkInput: AnalysisInput) = {
    val layerReferences = findViolations(checkInput.packages, checkInput.analysisPlan)
    new LayerViolationsResult(layerReferences, checkInput.layeringExpectations().violationsThreshold)
  }

  override def isWanted(expectations: Expectations) = expectations.layering != null

  private def findViolations(packages: List[Package], configuration: AnalysisPlan): List[LayerReference] = {

    val layers = configuration.expectations.layering.layers.map(configuration.basePackage + "." + _).toList

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