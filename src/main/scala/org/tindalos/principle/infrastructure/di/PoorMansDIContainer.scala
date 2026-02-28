package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.{AnalysisPlanValidatorImpl, ApplicationModule}
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesAnalyzer
import org.tindalos.principle.domain.analyzers.adp.CycleDetector
import org.tindalos.principle.domain.analyzers.layering.LayerViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sap.SAPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sdp.SDPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.structure.Graph.Node
import org.tindalos.principle.domain.analyzers.structure._
import org.tindalos.principle.domain.analyzers.submodulesblueprint.{SubmodulesBlueprintAnalyzer, SubmodulesBuilder}
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyAnalyzer
import org.tindalos.principle.domain.core.PackageStructureBuilder
import org.tindalos.principle.domain.resultprocessing.reporter.{AnalysisResultsReporter, Printer}
import org.tindalos.principle.domain.{AnalysisRunner, AnalysisRunnerImpl}
import org.tindalos.principle.infrastructure.analyzers.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.reporters.packagestructure.PlainEnglishPackageCohesionReporter
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner
import org.tindalos.principle.infrastructure.{JDependBasedPackageListBuilder, PackageStructureBuilderImpl}

object PoorMansDIContainer {


  def buildAnalyzer(rootPackage: String, printer:Printer) = {

    val buildNodesFn:String => Set[Node] = MyJDependRunner.createNodesOfClasses(_)

    val reporter = new AnalysisResultsReporter(
      new PlainEnglishADPAnalysisResultReporter(),
      new PlainEnglishLayerAnalysisResultReporter(),
      new PlainEnglishThirdPartyAnalysisResultReporter(),
      new PlainEnglishSAPAnalysisResultReporter(),
      new PlainEnglishComponentDependencyAnalysisResultReporter(),
      new PlainEnglishSubmodulesBlueprintAnalysisResultReporter(),
      new PlainEnglishSDPAnalysisResultReporter(),
      new PlainEnglishPackageCohesionReporter()
    )

    ApplicationModule.buildApplicationFn(
      new AnalysisPlanValidatorImpl,
      new JDependBasedPackageListBuilder(rootPackage),
      buildNodesFn,
      buildAnalysisRunner(),
      reporter,
      printer)
  }

  def buildAnalysisRunner(): AnalysisRunner =
    new AnalysisRunnerImpl(createAnalyzers(new PackageStructureBuilderImpl()))

  private def createAnalyzers(packageStructureBuilder: PackageStructureBuilder) = {
    val submodulesBlueprintAnalyzer = new SubmodulesBlueprintAnalyzer(new SubmodulesBuilder(packageStructureBuilder,
      new YAMLBasedSubmodulesBlueprintProvider()))
    List(
      LayerViolationAnalyzer,
      ThirdPartyAnalyzer,
      new CycleDetector(packageStructureBuilder),
      SDPViolationAnalyzer,
      SAPViolationAnalyzer,
      new ComponentDependenciesAnalyzer(packageStructureBuilder),
      submodulesBlueprintAnalyzer,
      new PackageCohesionAnalyzer(
        PackageCohesionModule.componentsFromPackages
        , PackageStructureHints1Finder.makeGroups
        , Graph.findDetachableSubgraphs
        , CohesiveGroupsDiscoveryModule.collapseToLimit))
  }



}