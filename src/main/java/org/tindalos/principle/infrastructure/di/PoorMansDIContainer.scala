package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.domain.checker.DesignQualityCheckService
import org.tindalos.principle.domain.checker.DesignQualityDetectorsRunner
import org.tindalos.principle.domain.checker.DesignQualityDetectorsRunner
import org.tindalos.principle.domain.core.PackageSorter
import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.coredetector.ViolationsReporter
import org.tindalos.principle.domain.detector.acd.ACDDetector
import org.tindalos.principle.domain.detector.acd.ACDResult
import org.tindalos.principle.domain.detector.adp.ADPResult
import org.tindalos.principle.domain.detector.adp.CycleDetector
import org.tindalos.principle.domain.detector.adp.PackageStructureBuilder
import org.tindalos.principle.domain.detector.layering.LayerViolationDetector
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult
import org.tindalos.principle.domain.detector.sap.SAPResult
import org.tindalos.principle.domain.detector.sap.SAPViolationDetector
import org.tindalos.principle.domain.detector.sdp.SDPResult
import org.tindalos.principle.domain.detector.sdp.SDPViolationDetector
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleFactory
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintCheckResult
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintViolationDetector
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesFactory
import org.tindalos.principle.domain.resultprocessing.reporter.DesignQualityCheckResultsReporter
import org.tindalos.principle.infrastructure.detector.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
import org.tindalos.principle.infrastructure.reporters.ACDViolationsReporter
import org.tindalos.principle.infrastructure.reporters.ADPViolationsReporter
import org.tindalos.principle.infrastructure.reporters.LayerViolationsReporter
import org.tindalos.principle.infrastructure.reporters.SAPViolationsReporter
import org.tindalos.principle.infrastructure.reporters.SDPViolationsReporter
import org.tindalos.principle.infrastructure.reporters.SubmodulesBlueprintViolationsReporter
import org.tindalos.principle.infrastructure.service.jdepend.JDependPackageAnalyzer
import org.tindalos.principle.infrastructure.service.jdepend.JDependRunner
import org.tindalos.principle.infrastructure.service.jdepend.MetricsCalculator
import org.tindalos.principle.infrastructure.service.jdepend.PackageFactory
import org.tindalos.principle.infrastructure.service.jdepend.PackageListFactory
import com.google.common.collect.Lists
import org.tindalos.principle.app.service.Application

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

    // SubmoduleDefinitionsProvider submoduleDefinitionsProvider = new JSONBasedSubmodulesBlueprintProvider()
    val submoduleDefinitionsProvider = new YAMLBasedSubmodulesBlueprintProvider()
    val submoduleFactory = new SubmoduleFactory()
    val submodulesFactory = new SubmodulesFactory(packageStructureBuilder, submoduleDefinitionsProvider, submoduleFactory)
    val submodulesBlueprintViolationDetector = new SubmodulesBlueprintViolationDetector(submodulesFactory)

    val detectors = List(layerViolationDetector, cycleDetector, sdpViolationDetector, sapViolationDetector, acdDetector, submodulesBlueprintViolationDetector)
    val designQualityDetectorsRunner = new DesignQualityDetectorsRunner(detectors)
    new DesignQualityCheckService(packageAnalyzer, designQualityDetectorsRunner)
  }

  def getDesignCheckResultsReporter() = {

    val reporters:Map[Class[_ <: CheckResult], ViolationsReporter[_ <: CheckResult]] = Map(
      classOf[ADPResult] -> new ADPViolationsReporter(),
      classOf[LayerViolationsResult] -> new LayerViolationsReporter(),
      classOf[SDPResult] -> new SDPViolationsReporter(),
      classOf[SAPResult] -> new SAPViolationsReporter(),
      classOf[ACDResult] -> new ACDViolationsReporter(),
      classOf[SubmodulesBlueprintCheckResult] -> new SubmodulesBlueprintViolationsReporter())

    new DesignQualityCheckResultsReporter(reporters)
  }

  def getApplication(basePackage: String) = new Application(getDesignCheckService(basePackage), getDesignCheckResultsReporter())

}