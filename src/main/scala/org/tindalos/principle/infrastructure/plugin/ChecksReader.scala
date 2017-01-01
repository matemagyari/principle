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
    val yamlObject = {
      val yamlObjectJava = new Yaml().load(yamlText).asInstanceOf[java.util.Map[String, Object]]
      ListConverter.convert(yamlObjectJava)
    }
    val rootPackage = yamlObject("root_package").asInstanceOf[String]

    val checks = getYamlStructure(yamlObject, "checks").get

    val modules: Option[SubmodulesBlueprint] = getYamlStructure(checks, "modules")
      .map { modules ⇒
        val threshold = modules.get("violation_threshold").map(_.asInstanceOf[Int]).getOrElse(0)
        new SubmodulesBlueprint(fileLocation, threshold)
      }

    val layering: Layering =
      getYamlStructure(checks, "layering").map(toLayering).getOrElse(null)

    val thirdParty: Option[ThirdParty] =
      getYamlStructure(checks, "third_party_restrictions").map(toThirdParty)

    val packageCoupling = new PackageCoupling()
    getYamlStructure(checks, "package_coupling").foreach { pc ⇒
      pc.get("acd_threshold").foreach { threshold ⇒
        packageCoupling.racd = RACD(threshold.asInstanceOf[Double])
      }
      pc.get("cyclic_dependencies_threshold").foreach { threshold ⇒
        packageCoupling.adp = ADP(threshold.asInstanceOf[Int])
      }
    }
    yamlObject.get("structure_analysis_enabled").map { sa ⇒
      if (sa.asInstanceOf[Boolean]) {
        packageCoupling.grouping = new Grouping()
      }
    }

    (new Checks(layering, thirdParty, packageCoupling, modules), rootPackage)
  }

  private def toThirdParty(structure: Map[String, Object]): ThirdParty = {
    val barriersYaml: Seq[Map[String, Object]] = structure("allowed_libraries")
        .asInstanceOf[java.util.List[java.util.Map[String, Object]]]
        .asScala.to[Seq].map(javaMap ⇒ javaMap.asScala.toMap)

    def toBarrier(m: Map[String, Object]): Barrier =
      new Barrier(
        layer = m("layer").asInstanceOf[String],
        components = m("libraries")
            .asInstanceOf[java.util.List[String]]
            .asScala.to[Seq].mkString(","))

    new ThirdParty(
      barriers = barriersYaml.map(toBarrier).to[List],
      threshold = structure("violation_threshold").asInstanceOf[Int])
  }

  private def toLayering(structure: Map[String, Object]): Layering =
    new Layering(
      layers = getYamList(structure, "layers").get,
      threshold = structure.get("violation_threshold").map(_.asInstanceOf[Int]).getOrElse(0))

  private def getYamList(structure: Map[String, Object], field: String): Option[List[String]] =
    structure.get(field)
        .map(f ⇒ ListConverter.convert(f.asInstanceOf[java.util.List[String]]))

  private def getYamlStructure(structure: Map[String, Object], field: String): Option[Map[String, Object]] =
    structure.get(field)
        .map(f ⇒ ListConverter.convert(f.asInstanceOf[java.util.Map[String, Object]]))

  private def readYAML(fileLocation: String): String =
    try {
      FileUtils.readFileToString(new File(fileLocation))
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw new InvalidConfigurationException("problem with reading file from " + fileLocation);
    }

}
