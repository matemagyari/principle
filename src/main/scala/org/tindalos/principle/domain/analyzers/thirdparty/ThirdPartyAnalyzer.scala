package org.tindalos.principle.domain.analyzers.thirdparty

import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.constraints.{Barrier, Constraints}
import org.tindalos.principle.domain.core.packages.PackageReference

import scala.collection.JavaConverters._

object ThirdPartyAnalyzer extends Analyzer {

  override def analyze(checkInput: AnalysisInput): ThirdPartyViolationsResult =

    checkInput.thirdPartyExpectations()
        .map { thirdParty ⇒

          val barriers = thirdParty.barriers.asScala.toList
          val violationsList =
            if (barriers.isEmpty)
              List[(PackageReference, PackageReference)]()
            else {
              val layers = checkInput.layeringExpectations().get.layers.asScala.toList
              val basePackage = checkInput.analysisPlan.basePackage
              for (aPackage <- checkInput.packages
                   if (underBasePackage(aPackage.reference, basePackage));
                   layer = layerOf(layers, basePackage, aPackage);
                   if layer.isDefined;
                   referencedPackage <- aPackage.getOwnExternalPackageReferences();
                   if (outOfAllowedComponents(layer.get, layers, barriers, referencedPackage))
              ) yield (aPackage.reference, referencedPackage)
            }

          val violations = violationsList.groupBy(_._1).map { case (k, vs) => k -> vs.map(_._2).toSet }
          val javaViolations = violations.map { case (k, v) => k -> v.asJava }.asJava

          new ThirdPartyViolationsResult(javaViolations, thirdParty)
        }
        .getOrElse(new ThirdPartyViolationsResult(java.util.Collections.emptyMap(), null))

  private def allowedComponentsForLayer(
      layers: List[String],
      layer: String,
      barriers: List[Barrier]) = {
    val innerLayers = layers.dropWhile(_ != layer)
    barriers.filter(b => innerLayers.contains(b.layer)).flatMap(_.components().asScala)
  }

  private def outOfAllowedComponents(layer: String, layers: List[String], barriers: List[Barrier], referencedPackage: PackageReference) =
    !allowedComponentsForLayer(layers, layer, barriers).exists(referencedPackage.startsWith(_))


  private def layerOf(layers: List[String], basePackage: String, aPackage: Package) =
    layers.find(l => aPackage.reference.startsWith(s"${basePackage}.${l}"))


  private def underBasePackage(aPackage: PackageReference, basePackage: String) =
    aPackage.startsWith(basePackage)

  override def isEnabled(designQualityChecks: Constraints) = designQualityChecks.thirdParty().isPresent
}
