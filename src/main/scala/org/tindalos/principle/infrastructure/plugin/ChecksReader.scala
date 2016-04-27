package org.tindalos.principle.infrastructure.plugin

import java.io.IOException

import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.domain.expectations.cumulativedependency.RACD
import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException
import org.tindalos.principle.domain.util.ListConverter
import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq
import scala.io.Source


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

    val checks = getYamlStructure(yamlObject, "checks").get //todo

    val modules: SubmodulesBlueprint = getYamlStructure(checks, "modules")
      .map { modules ⇒
        val threshold = modules.get("threshold").map(_.asInstanceOf[Int]).getOrElse(0)
        new SubmodulesBlueprint(fileLocation, threshold)
      }
      .getOrElse(null)

    val layering: Layering =
      getYamlStructure(checks, "layering").map(toLayering).getOrElse(null)

    val thirdParty: ThirdParty =
      getYamlStructure(checks, "third_party").map(toThirdParty).getOrElse(null)

    val packageCoupling = new PackageCoupling()
    checks.get("racd_threshold").foreach { threshold ⇒
      packageCoupling.racd = new RACD(threshold.asInstanceOf[Double])
    }
    checks.get("adp_threshold").foreach { threshold ⇒
      packageCoupling.adp = new ADP(threshold.asInstanceOf[Int])
    }

    (new Checks(layering, thirdParty, packageCoupling, modules), rootPackage)
  }

  private def toThirdParty(structure: Map[String, Object]): ThirdParty = {
    val barriersYaml: Seq[Map[String, Object]] = structure("barriers")
        .asInstanceOf[java.util.List[java.util.Map[String, Object]]]
        .asScala.to[Seq].map(javaMap ⇒ javaMap.asScala.toMap)

    def toBarrier(m: Map[String, Object]): Barrier =
      new Barrier(
        layer = m("layer").asInstanceOf[String],
        components = m("components")
            .asInstanceOf[java.util.List[String]]
            .asScala.to[Seq].mkString(","))

    new ThirdParty(
      barriers = barriersYaml.map(toBarrier).to[List],
      threshold = structure("threshold").asInstanceOf[Int])
  }

  private def toLayering(structure: Map[String, Object]): Layering =
    new Layering(
      layers = getYamList(structure, "layers").get,
      threshold = structure.get("threshold").map(_.asInstanceOf[Int]).getOrElse(0))

  private def getYamList(structure: Map[String, Object], field: String): Option[List[String]] =
    structure.get(field)
        .map(f ⇒ ListConverter.convert(f.asInstanceOf[java.util.List[String]]))

  private def getYamlStructure(structure: Map[String, Object], field: String): Option[Map[String, Object]] =
    structure.get(field)
        .map(f ⇒ ListConverter.convert(f.asInstanceOf[java.util.Map[String, Object]]))

  private def readYAML(fileLocation: String): String =
    try {
      Source.fromInputStream(getClass.getResourceAsStream(fileLocation)).mkString
    } catch {
      case ex: IOException =>
        ex.printStackTrace()
        throw new InvalidConfigurationException("problem with reading file from " + fileLocation);
    }

}
