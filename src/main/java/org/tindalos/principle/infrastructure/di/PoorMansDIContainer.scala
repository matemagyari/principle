package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.service.{Application, InputValidator}
import org.tindalos.principle.domain.checker.{DetectorsRunner}
import org.tindalos.principle.domain.core.{AnalysisInput, Package, PackageSorter}
import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.detector.acd.ACDDetector
import org.tindalos.principle.domain.detector.adp.{CycleDetector, PackageStructureBuilder}
import org.tindalos.principle.domain.detector.layering.LayerViolationDetector
import org.tindalos.principle.domain.detector.sap.SAPViolationDetector
import org.tindalos.principle.domain.detector.sdp.SDPViolationDetector
import org.tindalos.principle.domain.detector.submodulesblueprint.{SubmoduleFactory, SubmodulesBlueprintViolationDetector, SubmodulesFactory}
import org.tindalos.principle.domain.detector.thirdparty.ThirdPartyDetector
import org.tindalos.principle.domain.resultprocessing.reporter.{Printer, AnalysisResultsReporter}
import org.tindalos.principle.infrastructure.detector.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.service.jdepend.{JDependPackageAnalyzer, JDependRunner, MetricsCalculator, PackageFactory, PackageListFactory}

object PoorMansDIContainer {


  def buildAnalyzer(basePackage: String, printer:Printer) =
    Application.buildApplication(InputValidator.validate, buildDesignChecker(basePackage), buildReporter(),printer)

  def buildDesignChecker(basePackage: String): (AnalysisInput => List[AnalysisResult]) = {

    val packageSorter = new PackageSorter()

    val packageStructureBuilder = PackageStructureBuilder.build(packageSorter)

    val detectors = buildDetectors(packageStructureBuilder)
    val calculateDesignQualityMetrics = DetectorsRunner.buildDetectorsRunner(detectors)

    val packageFactory = new PackageFactory(MetricsCalculator.calculate, basePackage)
    val packageListTransformer = PackageListFactory.buildPackageListFactory(packageFactory, packageSorter)
    val packageAnalyzer = JDependPackageAnalyzer.buildAnalyzer(JDependRunner.getAnalyzedPackagesUnder, packageListTransformer)
    (parameters: AnalysisInput) => {
      val packages = packageAnalyzer(parameters)
      calculateDesignQualityMetrics(packages, parameters)
    }
  }

  private def buildDetectors(buildPackageStructure: (List[Package], String) => Package) =
    List(
      LayerViolationDetector,
      ThirdPartyDetector,
      CycleDetector.buildInstance(buildPackageStructure),
      SDPViolationDetector,
      SAPViolationDetector,
      ACDDetector.buildInstance(buildPackageStructure),
      buildSubmodulesBlueprintViolationDetector(buildPackageStructure))


  private def buildSubmodulesBlueprintViolationDetector(buildPackageStructure: (List[Package], String) => Package) = {
    // SubmoduleDefinitionsProvider submoduleDefinitionsProvider = new JSONBasedSubmodulesBlueprintProvider()
    val submodulesFactory = SubmodulesFactory.buildInstance(buildPackageStructure, YAMLBasedSubmodulesBlueprintProvider.readSubmoduleDefinitions, SubmoduleFactory.buildModules)
    SubmodulesBlueprintViolationDetector.buildInstance(submodulesFactory)
  }


  private def buildReporter(): List[AnalysisResult] => List[(String, Boolean)] = {

    AnalysisResultsReporter.buildResultReporter(
      ADPViolationsReporter.report,
      LayerViolationsReporter.report,
      ThirdPartyViolationsReporter.report,
      SAPViolationsReporter.report,
      ACDViolationsReporter.report,
      SubmodulesBlueprintViolationsReporter.report,
      SDPViolationsReporter.report
    )
  }

}