package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.{AnalysisPlanValidatorImpl, ApplicationModule, Printer}
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesAnalyzer
import org.tindalos.principle.domain.analyzers.adp.CycleDetector
import org.tindalos.principle.domain.analyzers.layering.LayerViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sap.SAPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sdp.SDPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.structure.Node
import org.tindalos.principle.domain.analyzers.structure._
import org.tindalos.principle.domain.analyzers.submodulesblueprint.{SubmodulesBlueprintAnalyzer, SubmodulesBuilder}
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyAnalyzer
import org.tindalos.principle.domain.core.PackageStructureBuilder
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter
import org.tindalos.principle.domain.{AnalysisRunner, AnalysisRunnerImpl}
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.reporters.packagestructure.YAMLPackageCohesionAnalysisResultReporter
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner
import org.tindalos.principle.infrastructure.{JDependBasedPackageListBuilder, PackageStructureBuilderImpl}
import scala.collection.JavaConverters._

object PoorMansDIContainer {


  def buildAnalyzer(rootPackage: String, printer:Printer) = {

    val buildNodesFn:String => Set[Node] = rootPackage => MyJDependRunner.createNodesOfClasses(rootPackage).asScala.toSet

    val reporter = new AnalysisResultsReporter(
      new YAMLADPAnalysisResultReporter(),
      new YAMLLayerAnalysisResultReporter(),
      new YAMLThirdPartyAnalysisResultReporter(),
      new YAMLSAPAnalysisResultReporter(),
      new YAMLComponentDependencyAnalysisResultReporter(),
      new YAMLSubmodulesBlueprintAnalysisResultReporter(),
      new YAMLSDPAnalysisResultReporter(),
      new YAMLPackageCohesionAnalysisResultReporter()
    )

    val analysisRunner = buildAnalysisRunner()

    ApplicationModule.buildApplicationFn(
      new AnalysisPlanValidatorImpl,
      new JDependBasedPackageListBuilder(rootPackage),
      buildNodesFn,
      analysisRunner,
      reporter,
      printer)
  }

  def buildAnalysisRunner(): AnalysisRunner = {
    new AnalysisRunnerImpl(createAnalyzers(new PackageStructureBuilderImpl()).asJava)
  }

  private def createAnalyzers(packageStructureBuilder: PackageStructureBuilder): List[Analyzer] = {
    val submodulesBlueprintAnalyzer = new SubmodulesBlueprintAnalyzer(new SubmodulesBuilder(packageStructureBuilder))
    List(
      new LayerViolationAnalyzer(),
      new ThirdPartyAnalyzer(),
      new CycleDetector(packageStructureBuilder),
      new SDPViolationAnalyzer(),
      new SAPViolationAnalyzer(),
      new ComponentDependenciesAnalyzer(packageStructureBuilder),
      submodulesBlueprintAnalyzer,
      new PackageCohesionAnalyzer())
  }



}