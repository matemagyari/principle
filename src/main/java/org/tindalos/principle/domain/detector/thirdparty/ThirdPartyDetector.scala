package org.tindalos.principle.domain.detector.thirdparty

import org.tindalos.principle.domain.core.{Package, PackageReference}
import org.tindalos.principle.domain.coredetector.{AnalysisInput, Detector}
import org.tindalos.principle.domain.expectations.{Barrier, Expectations}

/**
 * Created by mate.magyari on 26/05/2014.
 */
object ThirdPartyDetector extends Detector {

  override def analyze(checkInput: AnalysisInput) = {

    val barriers = checkInput.thirdPartyExpectations().barriers

    val violations = if (barriers == null || barriers.isEmpty) {
      List[(PackageReference, PackageReference)]()
    } else {
      val layers = checkInput.layeringExpectations().layers
      val basePackage = checkInput.analysisPlan.basePackage
      for (aPackage <- checkInput.packages
           if (underBasePackage(aPackage.reference, basePackage));
           layer = layerOf(layers,basePackage, aPackage);
           if layer.isDefined;
           referencedPackage <- aPackage.getOwnExternalPackageReferences();
           if (outOfAllowedComponents(layer.get, layers, barriers, referencedPackage))
      ) yield (aPackage.reference, referencedPackage)

    }
    new ThirdPartyViolationsResult(violations, checkInput.thirdPartyExpectations())
  }

  private def allowedComponentsForLayer(layers: List[String], layer: String, barriers: List[Barrier]) = {
    val innerLayers = layers.dropWhile(_ != layer)
    barriers.filter(b => innerLayers.contains(b.layer)).flatMap(_.componentList())
  }

  private def outOfAllowedComponents(layer: String, layers: List[String], barriers: List[Barrier], referencedPackage: PackageReference) =
    !allowedComponentsForLayer(layers, layer, barriers).exists(referencedPackage.startsWith(_))


  private def layerOf(layers: List[String], basePackage: String, aPackage: Package) =
    layers.find(l => aPackage.reference.startsWith(s"${basePackage}.${l}"))


  private def underBasePackage(aPackage: PackageReference, basePackage: String) =
    aPackage.startsWith(basePackage)


  override def isWanted(designQualityExpectations: Expectations) = designQualityExpectations.thirdParty != null
}
