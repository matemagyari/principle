package org.tindalos.principle.infrastructure.detector.submodulesblueprint

import java.util

import org.tindalos.principle.domain.core.PackageReference
import org.tindalos.principle.domain.agents.submodulesblueprint.InvalidBlueprintDefinitionException
import org.tindalos.principle.domain.agents.submodulesblueprint.SubmoduleDefinition
import org.tindalos.principle.domain.agents.submodulesblueprint.SubmoduleId
import java.io.IOException
import org.apache.commons.io.FileUtils
import java.io.File
import org.yaml.snakeyaml.Yaml
import org.tindalos.principle.domain.agents.submodulesblueprint.SubmoduleDefinitions
import scala.collection.JavaConverters._

import scala.collection.immutable.Seq

object YAMLBasedSubmodulesBlueprintProvider {

  def readSubmoduleDefinitions(
      submodulesDefinitionLocation: String,
      basePackageName: String): SubmoduleDefinitions =
    processYAML(getYAML(submodulesDefinitionLocation), basePackageName)

  protected def processYAML(yamlText: String, basePackageName: String): SubmoduleDefinitions = {
    val yamlObject = new Yaml().load(yamlText).asInstanceOf[util.Map[String, Object]].asScala.toMap
    processYaml(basePackageName, yamlObject)
  }

  def processYaml(basePackageName: String, yamlObject: Map[String, Object]): SubmoduleDefinitions = {

    val checks: Map[String, Object] =
      yamlObject("checks")
          .asInstanceOf[java.util.Map[String, Object]]
          .asScala.toMap

    val modules: Map[String, Object] =
      checks("modules")
          .asInstanceOf[java.util.Map[String, Object]]
          .asScala.toMap

    val submoduleDefinitionMap = buildSubmoduleDefinitions(modules, basePackageName)
    addDependencies(modules, submoduleDefinitionMap)

    new SubmoduleDefinitions(submoduleDefinitionMap)
  }

  private def checkSubmoduleExists(validSubmodules: Set[SubmoduleId], submoduleId: SubmoduleId) =
    if (!validSubmodules.contains(submoduleId)) throw new InvalidBlueprintDefinitionException("No submodules defined with id " + submoduleId);

  private def transformToSubmoduleIds(dependencies: Seq[String], validSubmodules: Set[SubmoduleId]) = {

    val ids = dependencies.map(new SubmoduleId(_))
    ids.foreach(checkSubmoduleExists(validSubmodules, _))
    ids
  }

  private def addDependencies(yamlObject: Map[String, Object], submoduleDefinitionMap: Map[SubmoduleId, SubmoduleDefinition]) = {

    val dependenciesOpt = yamlObject.get("module-dependencies")
    if (dependenciesOpt.isEmpty) throw new InvalidBlueprintDefinitionException("Submodule dependencies not defined! ")

    val dependencies: Map[String, Seq[String]] = dependenciesOpt.map { d ⇒
      d.asInstanceOf[java.util.LinkedHashMap[String, java.util.List[String]]]
          .asScala.toMap
          .map { case (k, v) ⇒ (k, v.asScala.to[List]) }
    }.getOrElse(Map.empty)

    dependencies.foreach { keyVal =>
      val submoduleId = new SubmoduleId(keyVal._1)
      checkSubmoduleExists(submoduleDefinitionMap.keySet, submoduleId)
      val submoduleDefinition = submoduleDefinitionMap.get(submoduleId).get
      val plannedDependencies = transformToSubmoduleIds(keyVal._2, submoduleDefinitionMap.keySet)
      submoduleDefinition.addPlannedDependencies(plannedDependencies)
    }
  }

  private def buildSubmoduleDefinitions(yamlObject: Map[String, Object], basePackageName: String) = {

    val definitionsOpt = yamlObject.get("module-definitions")
    if (definitionsOpt.isEmpty) throw new InvalidBlueprintDefinitionException("Submodules not defined! ")

    val definitions: Map[String, Seq[String]] = definitionsOpt.map { ds ⇒
      ds.asInstanceOf[java.util.LinkedHashMap[String, java.util.List[String]]]
          .asScala.toMap
          .map { case (k, v) ⇒ (k, v.asScala.to[List]) }
    }.getOrElse(Map.empty)

    definitions.map { definition ⇒
      val submoduleId = new SubmoduleId(definition._1)
      val packages = transformToPackageReferences(definition._2, basePackageName)
      val submoduleDefinition = new SubmoduleDefinition(submoduleId, packages.toSet)
      (submoduleId, submoduleDefinition)
    }
  }

  private def transformToPackageReferences(packageNames: Seq[String], basePackageName: String) = packageNames.map(x => new PackageReference(basePackageName + "." + x))

  protected def getYAML(submodulesDefinitionLocation: String) =
    try {
      FileUtils.readFileToString(new File(submodulesDefinitionLocation))
    } catch {
      case ex: IOException => throw new InvalidBlueprintDefinitionException("problem with reading file from " + submodulesDefinitionLocation);
    }
}