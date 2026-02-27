package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.{AnalysisPlanValidatorImpl, ApplicationModule}
import org.tindalos.principle.domain.agentscore.Analyzer
import org.tindalos.principle.domain.analyzers.acd.ACDAgent
import org.tindalos.principle.domain.analyzers.adp.CycleDetector
import org.tindalos.principle.domain.analyzers.layering.LayerViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sap.SAPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sdp.SDPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.structure.Graph.Node
import org.tindalos.principle.domain.analyzers.structure._
import org.tindalos.principle.domain.analyzers.submodulesblueprint.{SubmoduleFactory, SubmodulesBlueprintAnalyzer, SubmodulesFactoryBuilder}
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyAnalyzer
import org.tindalos.principle.domain.core.PackageStructureBuilder
import org.tindalos.principle.domain.resultprocessing.reporter.{AnalysisResultsReporter, Printer}
import org.tindalos.principle.domain.{AnalysisResult, AnalysisRunner, AnalysisRunnerImpl}
import org.tindalos.principle.infrastructure.analyzers.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner
import org.tindalos.principle.infrastructure.{JDependBasedPackageListBuilder, PackageStructureBuilderImpl}

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
    val packageStructureBuilder = new PackageStructureBuilderImpl()
    val analyzers: List[Analyzer] = createAnalyzers(packageStructureBuilder)
    new AnalysisRunnerImpl(analyzers)
  }

  private def createAnalyzers(packageStructureBuilder: PackageStructureBuilder) =
    List(
      LayerViolationAnalyzer,
      ThirdPartyAnalyzer,
      CycleDetector.buildAgent(packageStructureBuilder),
      SDPViolationAnalyzer,
      SAPViolationAnalyzer,
      ACDAgent.buildAgent(packageStructureBuilder),
      buildSubmodulesBlueprintViolationDetector(packageStructureBuilder),
      PackageCohesionDetector.buildAgent(
        PackageCohesionModule.componentsFromPackages
      , PackageStructureHints1Finder.makeGroups
      , Graph.findDetachableSubgraphs
      , CohesiveGroupsDiscoveryModule.collapseToLimit))


  private def buildSubmodulesBlueprintViolationDetector(packageStructureBuilder: PackageStructureBuilder) = {
    val submodulesFactory = SubmodulesFactoryBuilder.buildInstance(
      packageStructureBuilder,
      new YAMLBasedSubmodulesBlueprintProvider(),
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