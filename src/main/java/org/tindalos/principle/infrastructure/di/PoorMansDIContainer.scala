package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.service.{Application, InputValidator}
import org.tindalos.principle.domain.checker.{DesignQualityCheckResults, DesignQualityDetectorsRunner}
import org.tindalos.principle.domain.core.{DesignQualityCheckConfiguration, Package, PackageSorter}
import org.tindalos.principle.domain.detector.acd.ACDDetector
import org.tindalos.principle.domain.detector.adp.{CycleDetector, PackageStructureBuilder}
import org.tindalos.principle.domain.detector.layering.LayerViolationDetector
import org.tindalos.principle.domain.detector.sap.SAPViolationDetector
import org.tindalos.principle.domain.detector.sdp.SDPViolationDetector
import org.tindalos.principle.domain.detector.submodulesblueprint.{SubmoduleFactory, SubmodulesBlueprintViolationDetector, SubmodulesFactory}
import org.tindalos.principle.domain.detector.thirdparty.ThirdPartyDetector
import org.tindalos.principle.domain.resultprocessing.reporter.DesignQualityCheckResultsReporter
import org.tindalos.principle.infrastructure.detector.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.service.jdepend.{JDependPackageAnalyzer, JDependRunner, MetricsCalculator, PackageFactory, PackageListFactory}

object PoorMansDIContainer {

  def buildDesignChecker(basePackage: String): (DesignQualityCheckConfiguration => DesignQualityCheckResults) = {

    val packageSorter = new PackageSorter()

    val packageStructureBuilder = PackageStructureBuilder.build(packageSorter)

    val detectors = buildDetectors(packageStructureBuilder)
    val calculateDesignQualityMetrics = DesignQualityDetectorsRunner.buildDetectorsRunner(detectors)

    (parameters: DesignQualityCheckConfiguration) => {
      val packageFactory = new PackageFactory(MetricsCalculator.calculate, basePackage)
      val packageListTransformer = PackageListFactory.buildPackageListFactory(packageFactory, packageSorter)
      val packageAnalyzer = JDependPackageAnalyzer.buildAnalyzer(JDependRunner.getAnalyzedPackagesUnder, packageListTransformer)
      val packages = packageAnalyzer(parameters)
      calculateDesignQualityMetrics(packages, parameters)
    }
  }

  def buildDetectors(buildPackageStructure: (List[Package], String) => Package) =
    List(
      LayerViolationDetector,
      ThirdPartyDetector,
      CycleDetector.buildInstance(buildPackageStructure),
      SDPViolationDetector,
      SAPViolationDetector,
      ACDDetector.buildInstance(buildPackageStructure),
      buildSubmodulesBlueprintViolationDetector(buildPackageStructure))


  def buildSubmodulesBlueprintViolationDetector(buildPackageStructure: (List[Package], String) => Package) = {
    // SubmoduleDefinitionsProvider submoduleDefinitionsProvider = new JSONBasedSubmodulesBlueprintProvider()
    val submoduleDefinitionsProvider = new YAMLBasedSubmodulesBlueprintProvider()
    val submodulesFactory = new SubmodulesFactory(buildPackageStructure, submoduleDefinitionsProvider, SubmoduleFactory.buildModules)
    SubmodulesBlueprintViolationDetector.buildInstance(submodulesFactory)
  }


  def buildReporter(): (DesignQualityCheckResults) => List[(String, Boolean)] = {

    DesignQualityCheckResultsReporter.buildResultReporter(
      ADPViolationsReporter.report,
      LayerViolationsReporter.report,
      ThirdPartyViolationsReporter.report,
      SAPViolationsReporter.report,
      ACDViolationsReporter.report,
      SubmodulesBlueprintViolationsReporter.report,
      SDPViolationsReporter.report
    )
  }


  def getApplication(basePackage: String) =
    Application.buildApplication(buildDesignChecker(basePackage), buildReporter(), InputValidator.validate)

}