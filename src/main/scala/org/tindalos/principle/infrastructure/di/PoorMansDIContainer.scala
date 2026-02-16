package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.service.{ApplicationModule, InputValidator, InputValidatorImpl}
import org.tindalos.principle.domain.{AnalysisResult, AnalysisRunner, AnalysisRunnerImpl}
import org.tindalos.principle.domain.agentscore.{AnalysisInput, Analyzer}
import org.tindalos.principle.domain.analyzers.acd.ACDAgent
import org.tindalos.principle.domain.analyzers.adp.{CycleDetector, PackageStructureModule}
import org.tindalos.principle.domain.analyzers.layering.LayerViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sap.SAPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sdp.SDPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.structure.Graph.Node
import org.tindalos.principle.domain.analyzers.structure._
import org.tindalos.principle.domain.analyzers.submodulesblueprint.{SubmoduleFactory, SubmodulesBlueprintAgent, SubmodulesFactory}
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyAnalyzer
import org.tindalos.principle.domain.core.logging.TheLogger
import org.tindalos.principle.domain.core.{Package, PackageSorterModule}
import org.tindalos.principle.domain.resultprocessing.reporter.{AnalysisResultsReporter, Printer}
import org.tindalos.principle.infrastructure.detector.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner
import org.tindalos.principle.infrastructure.service.jdepend.{JDependPackageAnalyzer, JDependRunner, PackageFactory}

object PoorMansDIContainer {


  def buildAnalyzer(rootPackage: String, printer:Printer) = {

    val buildNodesFn:String => Set[Node] = MyJDependRunner.createNodesOfClasses(_)

    val inputValidator = new InputValidatorImpl
    ApplicationModule.buildApplicationFn(
      inputValidator.validate,
      buildPackageListProducerFn(rootPackage),
      buildNodesFn,
      buildAnalysisRunner(),
      buildReporter(),
      printer)
  }

  def buildPackageListProducerFn(rootPackage: String): (String) => List[Package] = {
    val packageFactory = new PackageFactory(rootPackage)
    val packageListTransformer = packageFactory.buildPackageListFactory(PackageSorterModule.sortByName(_))
    JDependPackageAnalyzer.buildAnalyzerFn(JDependRunner.preparePackages, packageListTransformer)
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
    val submodulesFactory = SubmodulesFactory.buildInstance(
      buildPackageStructure,
      YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions,
      SubmoduleFactory.buildModules)
    SubmodulesBlueprintAgent.buildInstance(submodulesFactory)
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