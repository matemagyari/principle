package org.tindalos.principle.infrastructure.plugin

import java.io.File

import org.apache.commons.io.FileUtils
import org.tindalos.principle.domain.constraints._
import org.tindalos.principle.domain.constraints.exception.InvalidConfigurationException
import org.tindalos.principle.domain.core.AnalysisPlan
import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConverters._


object ConstraintsReader {

  //under src/main/resources
  private val defaultFileLocation = "/principle.yml"

  def readFromFile(fileLocation: Option[String]): AnalysisPlan = {
    val location = fileLocation.getOrElse(defaultFileLocation)
    fromYaml(readYAML(location), location)
  }

  private def fromYaml(yamlText: String, fileLocation: String): AnalysisPlan = {
    val yamlObject =
      new Yaml().load(yamlText).asInstanceOf[java.util.Map[String, Object]].asScala.toMap

    val rootPackage = yamlObject("root_package").asInstanceOf[String]

    val checksYaml = getYamlStructure(yamlObject, "checks").get

    val checks = {

      val modules: Option[SubmodulesBlueprint] = getYamlStructure(checksYaml, "modules")
          .map { modules ⇒
            val threshold = modules.get("violation_threshold").map(_.asInstanceOf[Int]).getOrElse(0)
            new SubmodulesBlueprint(fileLocation, threshold)
          }

      val packageCoupling = {
        val x: Option[(Option[RACD], Option[ADP])] = getYamlStructure(checksYaml, "package_coupling").map { pc ⇒

          val racdTh = pc.get("acd_threshold").map { threshold ⇒
            new RACD(threshold.asInstanceOf[Double])
          }
          val adpTh = pc.get("cyclic_dependencies_threshold").map { threshold ⇒
            new ADP(threshold.asInstanceOf[Int])
          }

          (racdTh, adpTh)
        }

        val grouping = yamlObject.get("structure_analysis_enabled")
            .filter(_.asInstanceOf[Boolean])
            .map { _ ⇒ Grouping.of()}

        val builder = PackageCouplingConstraints.builder()
        x.flatMap(_._2).foreach(adp => builder.adp(adp))
        x.flatMap(_._1).foreach(racd => builder.racd(racd))
        grouping.foreach(g => builder.grouping(g))
        builder.build()
      }

      new Constraints(
        getYamlStructure(checksYaml, "layering").map(toLayering).orNull,
        getYamlStructure(checksYaml, "third_party_restrictions").map(toThirdParty).map(tp => java.util.Optional.of(tp)).getOrElse(java.util.Optional.empty()),
        java.util.Optional.of(packageCoupling),
        modules.map(sm => java.util.Optional.of(sm)).getOrElse(java.util.Optional.empty()))
    }

    new AnalysisPlan(checks, rootPackage)
  }

  private def toThirdParty(structure: Map[String, Object]): ThirdParty = {
    val barriersYaml: Seq[Map[String, Object]] = structure("allowed_libraries")
        .asInstanceOf[java.util.List[java.util.Map[String, Object]]]
        .asScala.to[Seq].map(javaMap ⇒ javaMap.asScala.toMap)

    def toBarrier(m: Map[String, Object]): Barrier =
      new Barrier(
        m("layer").asInstanceOf[String],
        m("libraries").asInstanceOf[java.util.List[String]])

    new ThirdParty(
      barriersYaml.map(toBarrier).asJava,
      structure("violation_threshold").asInstanceOf[Int])
  }

  private def toLayering(structure: Map[String, Object]): Layering =
    new Layering(
      getYamList(structure, "layers").map(_.asJava).getOrElse(java.util.List.of()),
      structure.get("violation_threshold").map(_.asInstanceOf[Int]).getOrElse(0))

  private def getYamList(structure: Map[String, Object], field: String): Option[List[String]] =
    structure.get(field)
        .map(_.asInstanceOf[java.util.List[String]].asScala.to[List])

  private def getYamlStructure(structure: Map[String, Object], field: String): Option[Map[String, Object]] =
    structure.get(field)
        .map(_.asInstanceOf[java.util.Map[String, Object]].asScala.toMap)

  private def readYAML(fileLocation: String): String =
    try {
      FileUtils.readFileToString(new File(fileLocation))
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw new InvalidConfigurationException("problem with reading file from " + fileLocation);
    }

}
