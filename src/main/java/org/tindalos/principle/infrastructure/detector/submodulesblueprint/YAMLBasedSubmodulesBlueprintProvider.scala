package org.tindalos.principle.infrastructure.detector.submodulesblueprint

import org.tindalos.principle.domain.core.PackageReference
import org.tindalos.principle.domain.detector.submodulesblueprint.InvalidBlueprintDefinitionException
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinition
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinitionsProvider
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleId
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleId
import java.io.IOException
import org.apache.commons.io.FileUtils
import java.io.File
import org.yaml.snakeyaml.Yaml
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinitions
import org.tindalos.principle.domain.util.ListConverter
import org.tindalos.principle.domain.util.ListConverter

class YAMLBasedSubmodulesBlueprintProvider extends SubmoduleDefinitionsProvider {

  def readSubmoduleDefinitions(submodulesDefinitionLocation: String, basePackageName: String) = {
    val yaml = YAMLBasedSubmodulesBlueprintProvider.getYAML(submodulesDefinitionLocation)
    YAMLBasedSubmodulesBlueprintProvider.processYAML(yaml, basePackageName)
  }
}

object YAMLBasedSubmodulesBlueprintProvider {

  protected def processYAML(yamlText: String, basePackageName: String) = {

    val yamlObjectJava = new Yaml().load(yamlText).asInstanceOf[java.util.Map[String, Object]]
    val yamlObject = ListConverter.convert(yamlObjectJava)
    val submoduleDefinitionMap = buildSubmoduleDefinitions(yamlObject, basePackageName)

    addDependencies(yamlObject, submoduleDefinitionMap)

    new SubmoduleDefinitions(submoduleDefinitionMap)
  }

  private def checkSubmoduleExists(validSubmodules: Set[SubmoduleId], submoduleId: SubmoduleId) =
    if (!validSubmodules.contains(submoduleId)) throw new InvalidBlueprintDefinitionException("No submodules defined with id " + submoduleId);

  private def transformToSubmoduleIds(dependencies: List[String], validSubmodules: Set[SubmoduleId]) = {

    val ids = dependencies.map(new SubmoduleId(_))
    ids.foreach(checkSubmoduleExists(validSubmodules, _))
    ids
  }

  private def addDependencies(yamlObject: Map[String, Object], submoduleDefinitionMap: Map[SubmoduleId, SubmoduleDefinition]) = {

    val dependenciesOpt = yamlObject.get("subdmodule-dependencies")
    if (dependenciesOpt.isEmpty) throw new InvalidBlueprintDefinitionException("Submodule dependencies not defined! ")
    val dependencies: Map[String, List[String]] = ListConverter.convert(dependenciesOpt.get.asInstanceOf[java.util.LinkedHashMap[String, java.util.List[String]]])

    dependencies.foreach({ keyVal =>
      val submoduleId = new SubmoduleId(keyVal._1)
      checkSubmoduleExists(submoduleDefinitionMap.keySet, submoduleId)
      val submoduleDefinition = submoduleDefinitionMap.get(submoduleId).get
      val plannedDependencies = transformToSubmoduleIds(keyVal._2, submoduleDefinitionMap.keySet)
      submoduleDefinition.addPlannedDependencies(plannedDependencies)
    })
  }

  private def buildSubmoduleDefinitions(yamlObject: Map[String, Object], basePackageName: String) = {

    val definitionsOpt = yamlObject.get("subdmodule-definitions")
    if (definitionsOpt.isEmpty) throw new InvalidBlueprintDefinitionException("Submodules not defined! ")
    
    val definitions: Map[String, List[String]] = ListConverter.convert(definitionsOpt.get.asInstanceOf[java.util.LinkedHashMap[String, java.util.List[String]]])

    for (keyVal <- definitions) yield {
      val submoduleId = new SubmoduleId(keyVal._1)
      val packages = transformToPackageReferences(keyVal._2, basePackageName)
      val submoduleDefinition = new SubmoduleDefinition(submoduleId, packages.toSet)
      (submoduleId, submoduleDefinition)
    }
  }

  private def transformToPackageReferences(packageNames: List[String], basePackageName: String) = packageNames.map(x => new PackageReference(basePackageName + "." + x))

  protected def getYAML(submodulesDefinitionLocation: String) =
    try {
      FileUtils.readFileToString(new File(submodulesDefinitionLocation))
    } catch {
      case ex: IOException => throw new InvalidBlueprintDefinitionException("problem with reading file from " + submodulesDefinitionLocation);
    }
}