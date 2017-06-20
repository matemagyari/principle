package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.service.{ApplicationModule, InputValidator}
import org.tindalos.principle.domain.checker.AgentsRunner
import org.tindalos.principle.domain.core.{Package, PackageSorterModule}
import org.tindalos.principle.domain.agentscore.{AnalysisInput, AnalysisResult}
import org.tindalos.principle.domain.agents.acd.ACDAgent
import org.tindalos.principle.domain.agents.adp.{CycleDetector, PackageStructureModule}
import org.tindalos.principle.domain.agents.layering.LayerViolationAgent
import org.tindalos.principle.domain.agents.sap.SAPViolationAgent
import org.tindalos.principle.domain.agents.sdp.SDPViolationAgent
import org.tindalos.principle.domain.agents.structure.Graph.Node
import org.tindalos.principle.domain.agents.structure._
import org.tindalos.principle.domain.agents.submodulesblueprint.{SubmoduleFactory, SubmodulesBlueprintAgent, SubmodulesFactory}
import org.tindalos.principle.domain.agents.thirdparty.ThirdPartyAgent
import org.tindalos.principle.domain.resultprocessing.reporter.{AnalysisResultsReporter, Printer}
import org.tindalos.principle.infrastructure.detector.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner
import org.tindalos.principle.infrastructure.service.jdepend.{JDependPackageAnalyzer, JDependRunner, PackageFactory}

import scala.collection.immutable.Seq

object PoorMansDIContainer {


  def buildAnalyzer(rootPackage: String, printer:Printer) = {

    val buildNodesFn:String => Set[Node] = MyJDependRunner.createNodesOfClasses(_)

    ApplicationModule.buildApplicationFn(
      InputValidator.validate,
      buildPackageListProducerFn(rootPackage),
      buildNodesFn,
      buildRunAnalysisFn(),
      buildReporter(),
      printer)
  }

  def buildPackageListProducerFn(rootPackage: String): (String) => Seq[Package] = {
    val packageFactory = new PackageFactory(rootPackage)
    val packageListTransformer = packageFactory.buildPackageListFactory(PackageSorterModule.sortByName(_))
    JDependPackageAnalyzer.buildAnalyzerFn(JDependRunner.preparePackages, packageListTransformer)
  }

  def buildRunAnalysisFn(): AnalysisInput => Seq[AnalysisResult] = {
    val packageStructureBuilder = PackageStructureModule.createBuilder(PackageSorterModule.sortByName(_, _))
    val detectors = createDetectors(packageStructureBuilder)
    AgentsRunner.buildAgentsRunner(detectors)
  }

  private def createDetectors(buildPackageStructure: (Seq[Package], String) => Package) =
    List(
      LayerViolationAgent,
      ThirdPartyAgent,
      CycleDetector.buildAgent(buildPackageStructure),
      SDPViolationAgent,
      SAPViolationAgent,
      ACDAgent.buildAgent(buildPackageStructure),
      buildSubmodulesBlueprintViolationDetector(buildPackageStructure),
      PackageCohesionDetector.buildAgent(
        PackageCohesionModule.componentsFromPackages
      , PackageStructureHints1Finder.makeGroups
      , Graph.findDetachableSubgraphs
      , CohesiveGroupsDiscoveryModule.collapseToLimit))


  private def buildSubmodulesBlueprintViolationDetector(buildPackageStructure: (Seq[Package], String) => Package) = {
    val submodulesFactory = SubmodulesFactory.buildInstance(
      buildPackageStructure,
      YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions,
      SubmoduleFactory.buildModules)
    SubmodulesBlueprintAgent.buildInstance(submodulesFactory)
  }


  private def buildReporter(): Seq[AnalysisResult] => Seq[(String, Boolean)] = {

    AnalysisResultsReporter.buildResultReporter(
      ADPViolationsReporter.report,
      LayerViolationsReporter.report,
      ThirdPartyViolationsReporter.report,
      SAPViolationsReporter.report,
      ACDViolationsReporter.report,
      SubmodulesBlueprintViolationsReporter.report,
      SDPViolationsReporter.report,
      PackageCohesionReporter.report
    )
  }

}