package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.service.{InputValidator, Application}
import org.tindalos.principle.domain.checker.{DesignQualityCheckResults, DesignQualityDetectorsRunner}
import org.tindalos.principle.domain.core.{Package, DesignQualityCheckConfiguration, PackageSorter}
import org.tindalos.principle.domain.coredetector.{Detector, CheckResult, ViolationsReporter}
import org.tindalos.principle.domain.detector.acd.{ACDDetector, ACDResult}
import org.tindalos.principle.domain.detector.adp.{ADPResult, CycleDetector, PackageStructureBuilder}
import org.tindalos.principle.domain.detector.layering.{LayerViolationDetector, LayerViolationsResult}
import org.tindalos.principle.domain.detector.sap.{SAPResult, SAPViolationDetector}
import org.tindalos.principle.domain.detector.sdp.{SDPResult, SDPViolationDetector}
import org.tindalos.principle.domain.detector.submodulesblueprint.{SubmoduleFactory, SubmodulesBlueprintCheckResult, SubmodulesBlueprintViolationDetector, SubmodulesFactory}
import org.tindalos.principle.domain.detector.thirdparty.{ThirdPartyDetector, ThirdPartyViolationsResult}
import org.tindalos.principle.domain.resultprocessing.reporter.DesignQualityCheckResultsReporter
import org.tindalos.principle.infrastructure.detector.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.service.jdepend.{JDependPackageAnalyzer, JDependRunner, MetricsCalculator, PackageFactory, PackageListFactory}

object PoorMansDIContainer {

  def buildDesignChecker(basePackage: String):(DesignQualityCheckConfiguration => DesignQualityCheckResults) = {

    val packageSorter = new PackageSorter()

    val packageStructureBuilder = PackageStructureBuilder.build(packageSorter)

    val detectors = buildDetectors(packageStructureBuilder)
    val designQualityDetectorsRunner = DesignQualityDetectorsRunner.buildDetectorsRunner(detectors)

    (parameters: DesignQualityCheckConfiguration) => {
      val packageFactory = new PackageFactory(MetricsCalculator.calculate, basePackage)
      val packageListTransformer = PackageListFactory.buildPackageListFactory(packageFactory, packageSorter)
      val packageAnalyzer = JDependPackageAnalyzer.buildAnalyzer(JDependRunner.getAnalyzedPackagesUnder, packageListTransformer)
      val packages = packageAnalyzer(parameters)
      designQualityDetectorsRunner(packages, parameters)
    }
  }

  def buildDetectors(packageStructureBuilder: (List[Package], String) => Package): List[Detector] = {
    val cycleDetector = new CycleDetector(packageStructureBuilder)
    val sdpViolationDetector = new SDPViolationDetector()
    val sapViolationDetector = new SAPViolationDetector()
    val acdDetector = new ACDDetector(packageStructureBuilder)
    val layerViolationDetector = new LayerViolationDetector()
    val thirdPartyDetector = new ThirdPartyDetector()

    val submodulesBlueprintViolationDetector = buildSubmodulesBlueprintViolationDetector(packageStructureBuilder)

    List(layerViolationDetector, thirdPartyDetector, cycleDetector, sdpViolationDetector, sapViolationDetector, acdDetector, submodulesBlueprintViolationDetector)
  }

  def buildSubmodulesBlueprintViolationDetector(packageStructureBuilder: (List[Package], String) => Package): SubmodulesBlueprintViolationDetector = {
    // SubmoduleDefinitionsProvider submoduleDefinitionsProvider = new JSONBasedSubmodulesBlueprintProvider()
    val submoduleDefinitionsProvider = new YAMLBasedSubmodulesBlueprintProvider()
    val submodulesFactory = new SubmodulesFactory(packageStructureBuilder, submoduleDefinitionsProvider, SubmoduleFactory.buildModules)
    new SubmodulesBlueprintViolationDetector(submodulesFactory)
  }

  def buildDesignCheckResultsReporter() = {

    val reporters:Map[Class[_ <: CheckResult], ViolationsReporter[_ <: CheckResult]] = Map(
      classOf[ADPResult] -> new ADPViolationsReporter(),
      classOf[LayerViolationsResult] -> new LayerViolationsReporter(),
      classOf[ThirdPartyViolationsResult] -> new ThirdPartyViolationsReporter(),
      classOf[SDPResult] -> new SDPViolationsReporter(),
      classOf[SAPResult] -> new SAPViolationsReporter(),
      classOf[ACDResult] -> new ACDViolationsReporter(),
      classOf[SubmodulesBlueprintCheckResult] -> new SubmodulesBlueprintViolationsReporter())

    DesignQualityCheckResultsReporter.buildResultReporter(reporters)
  }

  def getApplication(basePackage: String) =
    Application.buildApplication(buildDesignChecker(basePackage), buildDesignCheckResultsReporter(), InputValidator.validate)

}