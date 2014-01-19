package org.tindalos.principle.domain.detector.layering

import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.core.Package
import scala.collection.JavaConversions._
import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.expectations.DesignQualityExpectations

class LayerViolationDetector extends Detector {

  override def analyze(checkInput: CheckInput) = {
    val layerReferences: List[LayerReference] = findViolations(checkInput.packages, checkInput.designQualityCheckConfiguration)
    new LayerViolationsResult(layerReferences, checkInput.getLayeringExpectations())
  }
  
  override def isWanted(expectations:DesignQualityExpectations) = expectations.layering != null

  private def findViolations(packages: List[Package], configuration: DesignQualityCheckConfiguration): List[LayerReference] = {

    val violations = scala.collection.mutable.ListBuffer[LayerReference]()
    val layers = configuration.getLayeringExpectations().layers.map(configuration.basePackage + "." + _).toList

    val relevantPackages = packages.filter(_.reference.startsWith(configuration.basePackage))
    relevantPackages.foreach({ aPackage =>

      val layer = layers.find(aPackage.reference.startsWith(_))
      if (layer.isDefined) {
        val outerLayers = layers.slice(0, layers.indexOf(layer.get))
        violations.++=(getReferencesToLayers(aPackage, outerLayers, configuration.basePackage))
      }

    })

    violations.toList
  }

  private def getReferencesToLayers(aPackage: Package, layers: List[String], basePackage: String) = {
    val references = scala.collection.mutable.ListBuffer[LayerReference]()

    val referencedPackages = aPackage.getOwnPackageReferences().filter(_.startsWith(basePackage))

    for (referencedPackage <- referencedPackages; layer <- layers if referencedPackage.startsWith(layer)) {
      references.+=(new LayerReference(aPackage.reference.name, referencedPackage.name))
    }

    references.toList
  }

}