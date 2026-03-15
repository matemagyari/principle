package org.tindalos.principle.domain.analyzers.layering

import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.core.Package

import scala.collection.JavaConverters._

object LayerViolationAnalyzer extends Analyzer {

  override def analyze(checkInput: AnalysisInput): LayerViolationsResult = {
    val layering = checkInput.layeringExpectations().get
    val layerReferences = findViolations(checkInput.packages, checkInput.analysisPlan)
    new LayerViolationsResult(layerReferences.asJava, layering.violationThreshold)
  }

  override def isEnabled(expectations: Constraints) = expectations.layering.isPresent

  private def findViolations(packages: List[Package], configuration: AnalysisPlan): List[LayerReference] = {

    val layers = configuration.constraints.layering.get.layers.asScala.map(configuration.basePackage + "." + _).toList

    for (aPackage <- packages
            if aPackage.reference.startsWith(configuration.basePackage);
            layer = layers.find(aPackage.reference.startsWith(_))
            if layer.isDefined;
         referencedPackage <- aPackage.getOwnPackageReferences()
          .asScala
            if referencedPackage.startsWith(configuration.basePackage);
         referencedLayer <- layers.slice(0, layers.indexOf(layer.get))
            if referencedPackage.startsWith(referencedLayer)
    ) yield new LayerReference(aPackage.reference.name, referencedPackage.name)
  }

}