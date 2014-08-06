package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.service.{InputValidator, Application}
import org.tindalos.principle.domain.checker.{DesignQualityCheckService, DesignQualityDetectorsRunner}
import org.tindalos.principle.domain.core.PackageSorter
import org.tindalos.principle.domain.coredetector.{CheckResult, ViolationsReporter}
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

  def getDesignCheckService(basePackage: String) = {
    val jDependRunner = new JDependRunner()
    val packageFactory = new PackageFactory(new MetricsCalculator(), basePackage)
    val packageSorter = new PackageSorter()
    val packageListFactory = new PackageListFactory(packageFactory, packageSorter)
    val packageAnalyzer = new JDependPackageAnalyzer(jDependRunner, packageListFactory)

    val packageStructureBuilder = new PackageStructureBuilder(packageSorter)

    val cycleDetector = new CycleDetector(packageStructureBuilder)
    val sdpViolationDetector = new SDPViolationDetector()
    val sapViolationDetector = new SAPViolationDetector()
    val acdDetector = new ACDDetector(packageStructureBuilder)
    val layerViolationDetector = new LayerViolationDetector()
    val thirdPartyDetector = new ThirdPartyDetector()

    // SubmoduleDefinitionsProvider submoduleDefinitionsProvider = new JSONBasedSubmodulesBlueprintProvider()
    val submoduleDefinitionsProvider = new YAMLBasedSubmodulesBlueprintProvider()
    val submoduleFactory = new SubmoduleFactory()
    val submodulesFactory = new SubmodulesFactory(packageStructureBuilder, submoduleDefinitionsProvider, submoduleFactory)
    val submodulesBlueprintViolationDetector = new SubmodulesBlueprintViolationDetector(submodulesFactory)

    val detectors = List(layerViolationDetector, thirdPartyDetector, cycleDetector, sdpViolationDetector, sapViolationDetector, acdDetector, submodulesBlueprintViolationDetector)
    val designQualityDetectorsRunner = new DesignQualityDetectorsRunner(detectors)
    new DesignQualityCheckService(packageAnalyzer, designQualityDetectorsRunner)
  }

  def getDesignCheckResultsReporter() = {

    val reporters:Map[Class[_ <: CheckResult], ViolationsReporter[_ <: CheckResult]] = Map(
      classOf[ADPResult] -> new ADPViolationsReporter(),
      classOf[LayerViolationsResult] -> new LayerViolationsReporter(),
      classOf[ThirdPartyViolationsResult] -> new ThirdPartyViolationsReporter(),
      classOf[SDPResult] -> new SDPViolationsReporter(),
      classOf[SAPResult] -> new SAPViolationsReporter(),
      classOf[ACDResult] -> new ACDViolationsReporter(),
      classOf[SubmodulesBlueprintCheckResult] -> new SubmodulesBlueprintViolationsReporter())

    new DesignQualityCheckResultsReporter(reporters)
  }

  def getApplication(basePackage: String) = new Application(getDesignCheckService(basePackage), getDesignCheckResultsReporter(), new InputValidator())

}