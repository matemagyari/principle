error id: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/infrastructure/di/PoorMansDIContainer.scala:PrincipleApplication
file://<WORKSPACE>/src/main/scala/org/tindalos/principle/infrastructure/di/PoorMansDIContainer.scala
empty definition using pc, found symbol in pc: 
semanticdb not found

found definition using fallback; symbol PrincipleApplication
offset: 1573
uri: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/infrastructure/di/PoorMansDIContainer.scala
text:
```scala
package org.tindalos.guardrails.infrastructure.di

import org.tindalos.guardrails.app.{AnalysisPlanValidatorImpl, NodeBuilder, Printer, PrincipleApplication}
import org.tindalos.guardrails.domain.analyzers.Analyzer
import org.tindalos.guardrails.domain.analyzers.acd.ComponentDependenciesAnalyzer
import org.tindalos.guardrails.domain.analyzers.adp.CycleDetector
import org.tindalos.guardrails.domain.analyzers.layering.LayerViolationAnalyzer
import org.tindalos.guardrails.domain.analyzers.sap.SAPViolationAnalyzer
import org.tindalos.guardrails.domain.analyzers.sdp.SDPViolationAnalyzer
import org.tindalos.guardrails.domain.analyzers.structure._
import org.tindalos.guardrails.domain.analyzers.submodulesblueprint.{SubmodulesBlueprintAnalyzer, SubmodulesBuilder}
import org.tindalos.guardrails.domain.analyzers.thirdparty.ThirdPartyAnalyzer
import org.tindalos.guardrails.domain.core.PackageStructureBuilder
import org.tindalos.guardrails.app.reporters.AnalysisResultsReporter
import org.tindalos.guardrails.domain.{AnalysisRunner, AnalysisRunnerImpl}
import org.tindalos.guardrails.infrastructure.reporters._
import org.tindalos.guardrails.infrastructure.reporters.packagestructure.YAMLPackageCohesionAnalysisResultReporter
import org.tindalos.guardrails.infrastructure.service.jdepend.classdependencies.DefaultNodeBuilder
import org.tindalos.guardrails.infrastructure.{JDependBasedPackageListBuilder, PackageStructureBuilderImpl}
import scala.collection.JavaConverters._

object PoorMansDIContainer {


  def buildAnalyzer(rootPackage: String, printer:Printer): Princ@@ipleApplication = {

    val nodeBuilder: NodeBuilder = new DefaultNodeBuilder()

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