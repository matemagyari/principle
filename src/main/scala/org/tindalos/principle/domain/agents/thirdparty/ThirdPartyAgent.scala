package org.tindalos.principle.domain.agents.thirdparty

import org.tindalos.principle.domain.agentscore.{Agent, AnalysisInput}
import org.tindalos.principle.domain.core.{Package, PackageReference}
import org.tindalos.principle.domain.expectations.{Barrier, Checks}

import scala.collection.immutable.Seq

object ThirdPartyAgent extends Agent {

  override def analyze(checkInput: AnalysisInput) =

    checkInput.thirdPartyExpectations()
        .map { thirdParty ⇒

          val barriers = thirdParty.barriers
          val violations =
            if (barriers.isEmpty)
              Seq[(PackageReference, PackageReference)]()
            else {
              val layers = checkInput.layeringExpectations().layers
              val basePackage = checkInput.analysisPlan.basePackage
              for (aPackage <- checkInput.packages
                   if (underBasePackage(aPackage.reference, basePackage));
                   layer = layerOf(layers, basePackage, aPackage);
                   if layer.isDefined;
                   referencedPackage <- aPackage.getOwnExternalPackageReferences();
                   if (outOfAllowedComponents(layer.get, layers, barriers, referencedPackage))
              ) yield (aPackage.reference, referencedPackage)
            }

          ThirdPartyViolationsResult(violations, thirdParty)
        }
        .getOrElse(ThirdPartyViolationsResult(Seq.empty, null))

  private def allowedComponentsForLayer(
      layers: Seq[String],
      layer: String,
      barriers: Seq[Barrier]) = {
    val innerLayers = layers.dropWhile(_ != layer)
    barriers.filter(b ⇒ innerLayers.contains(b.layer)).flatMap(_.componentList())
  }

  private def outOfAllowedComponents(layer: String, layers: Seq[String], barriers: Seq[Barrier], referencedPackage: PackageReference) =
    !allowedComponentsForLayer(layers, layer, barriers).exists(referencedPackage.startsWith(_))


  private def layerOf(layers: Seq[String], basePackage: String, aPackage: Package) =
    layers.find(l ⇒ aPackage.reference.startsWith(s"${basePackage}.${l}"))


  private def underBasePackage(aPackage: PackageReference, basePackage: String) =
    aPackage.startsWith(basePackage)

  override def isWanted(designQualityChecks: Checks) = designQualityChecks.thirdParty.nonEmpty
}
