package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.{ApplicationModule, AnalysisPlanValidatorImpl}
import org.tindalos.principle.domain.agentscore.Analyzer
import org.tindalos.principle.domain.analyzers.acd.ACDAgent
import org.tindalos.principle.domain.analyzers.adp.{CycleDetector, PackageStructureModule}
import org.tindalos.principle.domain.analyzers.layering.LayerViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sap.SAPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sdp.SDPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.structure.Graph.Node
import org.tindalos.principle.domain.analyzers.structure._
import org.tindalos.principle.domain.analyzers.submodulesblueprint.{SubmoduleFactory, SubmodulesBlueprintAnalyzer, SubmodulesFactory}
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyAnalyzer
import org.tindalos.principle.domain.core.{Package, PackageSorterModule}
import org.tindalos.principle.domain.resultprocessing.reporter.{AnalysisResultsReporter, Printer}
import org.tindalos.principle.domain.{AnalysisResult, AnalysisRunner, AnalysisRunnerImpl}
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder
import org.tindalos.principle.infrastructure.analyzers.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner

object PoorMansDIContainer {


  def buildAnalyzer(rootPackage: String, printer:Printer) = {

    val buildNodesFn:String => Set[Node] = MyJDependRunner.createNodesOfClasses(_)

    ApplicationModule.buildApplicationFn(
      new AnalysisPlanValidatorImpl,
      new JDependBasedPackageListBuilder(rootPackage),
      buildNodesFn,
      buildAnalysisRunner(),
      buildReporter(),
      printer)
  }

  def buildAnalysisRunner(): AnalysisRunner = {
    val packageStructureBuilder = PackageStructureModule.createBuilder(PackageSorterModule.sortByName(_, _))
    val analyzers: List[Analyzer] = createAnalyzers(packageStructureBuilder)
    new AnalysisRunnerImpl(analyzers)
  }

  private def createAnalyzers(buildPackageStructure: (List[Package], String) => Package) =
    List(
      LayerViolationAnalyzer,
      ThirdPartyAnalyzer,
      CycleDetector.buildAgent(buildPackageStructure),
      SDPViolationAnalyzer,
      SAPViolationAnalyzer,
      ACDAgent.buildAgent(buildPackageStructure),
      buildSubmodulesBlueprintViolationDetector(buildPackageStructure),
      PackageCohesionDetector.buildAgent(
        PackageCohesionModule.componentsFromPackages
      , PackageStructureHints1Finder.makeGroups
      , Graph.findDetachableSubgraphs
      , CohesiveGroupsDiscoveryModule.collapseToLimit))


  private def buildSubmodulesBlueprintViolationDetector(buildPackageStructure: (List[Package], String) => Package) = {
    val readSubmoduleDefinitions = (submodulesDefinitionLocation: String, basePackageName: String) => {
      new YAMLBasedSubmodulesBlueprintProvider(basePackageName).readSubmoduleDefinitions(submodulesDefinitionLocation)
    }
    val submodulesFactory = SubmodulesFactory.buildInstance(
      buildPackageStructure,
      readSubmoduleDefinitions,
      SubmoduleFactory.buildModules)
    SubmodulesBlueprintAnalyzer.buildInstance(submodulesFactory)
  }


  private def buildReporter(): List[AnalysisResult] => List[(String, Boolean)] = {

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