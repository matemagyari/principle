error id: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/infrastructure/di/PoorMansDIContainer.scala:DefaultNodeBuilder
file://<WORKSPACE>/src/main/scala/org/tindalos/principle/infrastructure/di/PoorMansDIContainer.scala
empty definition using pc, found symbol in pc: 
semanticdb not found

found definition using fallback; symbol DefaultNodeBuilder
offset: 1633
uri: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/infrastructure/di/PoorMansDIContainer.scala
text:
```scala
package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.{AnalysisPlanValidatorImpl, NodeBuilder, Printer, PrincipleApplication}
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesAnalyzer
import org.tindalos.principle.domain.analyzers.adp.CycleDetector
import org.tindalos.principle.domain.analyzers.layering.LayerViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sap.SAPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.sdp.SDPViolationAnalyzer
import org.tindalos.principle.domain.analyzers.structure._
import org.tindalos.principle.domain.analyzers.submodulesblueprint.{SubmodulesBlueprintAnalyzer, SubmodulesBuilder}
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyAnalyzer
import org.tindalos.principle.domain.core.PackageStructureBuilder
import org.tindalos.principle.app.reporters.AnalysisResultsReporter
import org.tindalos.principle.domain.{AnalysisRunner, AnalysisRunnerImpl}
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.reporters.packagestructure.YAMLPackageCohesionAnalysisResultReporter
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.DefaultNodeBuilder
import org.tindalos.principle.infrastructure.{JDependBasedPackageListBuilder, PackageStructureBuilderImpl}
import scala.collection.JavaConverters._

object PoorMansDIContainer {


  def buildAnalyzer(rootPackage: String, printer:Printer): PrincipleApplication = {

    val nodeBuilder: NodeBuilder = new @@DefaultNodeBuilder()

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

    new PrincipleApplication(
      new AnalysisPlanValidatorImpl,
      new JDependBasedPackageListBuilder(rootPackage),
      nodeBuilder,
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
```


#### Short summary: 

empty definition using pc, found symbol in pc: 