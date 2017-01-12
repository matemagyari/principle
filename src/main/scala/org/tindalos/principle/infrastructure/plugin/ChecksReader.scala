package org.tindalos.principle.infrastructure.plugin

import java.io.File

import org.apache.commons.io.FileUtils
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException
import org.tindalos.principle.domain.util.ListConverter
import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq


object ChecksReader {

  //under src/main/resources
  private val defaultFileLocation = "/principle.yml"

  def readFromFile(fileLocation: Option[String]): (Checks, String) = {
    val location = fileLocation.getOrElse(defaultFileLocation)
    fromYaml(readYAML(location), location)
  }

  private def fromYaml(yamlText: String, fileLocation: String): (Checks, String) = {
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
            RACD(threshold.asInstanceOf[Double])
          }
          val adpTh = pc.get("cyclic_dependencies_threshold").map { threshold ⇒
            ADP(threshold.asInstanceOf[Int])
          }

          (racdTh, adpTh)
        }

        val grouping = yamlObject.get("structure_analysis_enabled")
            .filter(_.asInstanceOf[Boolean])
            .map { _ ⇒ new Grouping() }
            .getOrElse(null)

        PackageCoupling(
          racd = x.flatMap(_._1).getOrElse(null),
          adp = x.flatMap(_._2),
          grouping = grouping)
      }

      new Checks(
        layering = getYamlStructure(checksYaml, "layering").map(toLayering).getOrElse(null),
        thirdParty = getYamlStructure(checksYaml, "third_party_restrictions").map(toThirdParty),
        packageCoupling = Some(packageCoupling),
        modules)
    }

    (checks, rootPackage)
  }

  private def toThirdParty(structure: Map[String, Object]): ThirdParty = {
    val barriersYaml: Seq[Map[String, Object]] = structure("allowed_libraries")
        .asInstanceOf[java.util.List[java.util.Map[String, Object]]]
        .asScala.to[Seq].map(javaMap ⇒ javaMap.asScala.toMap)

    def toBarrier(m: Map[String, Object]): Barrier =
      Barrier(
        layer = m("layer").asInstanceOf[String],
        components = m("libraries")
            .asInstanceOf[java.util.List[String]]
            .asScala.to[Seq].mkString(","))

    ThirdParty(
      barriers = barriersYaml.map(toBarrier).to[List],
      threshold = structure("violation_threshold").asInstanceOf[Int])
  }

  private def toLayering(structure: Map[String, Object]): Layering =
    new Layering(
      layers = getYamList(structure, "layers").get,
      threshold = structure.get("violation_threshold").map(_.asInstanceOf[Int]).getOrElse(0))

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
