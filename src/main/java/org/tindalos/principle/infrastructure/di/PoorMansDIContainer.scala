package org.tindalos.principle.infrastructure.di

import org.tindalos.principle.app.service.{Application, InputValidator}
import org.tindalos.principle.domain.checker.DetectorsRunner
import org.tindalos.principle.domain.core.{Package, PackageSorter}
import org.tindalos.principle.domain.coredetector.{AnalysisInput, AnalysisResult}
import org.tindalos.principle.domain.detector.acd.ACDDetector
import org.tindalos.principle.domain.detector.adp.{CycleDetector, PackageStructureBuilder}
import org.tindalos.principle.domain.detector.layering.LayerViolationDetector
import org.tindalos.principle.domain.detector.sap.SAPViolationDetector
import org.tindalos.principle.domain.detector.sdp.SDPViolationDetector
import org.tindalos.principle.domain.detector.submodulesblueprint.{SubmoduleFactory, SubmodulesBlueprintViolationDetector, SubmodulesFactory}
import org.tindalos.principle.domain.detector.thirdparty.ThirdPartyDetector
import org.tindalos.principle.domain.resultprocessing.reporter.{AnalysisResultsReporter, Printer}
import org.tindalos.principle.infrastructure.detector.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.service.jdepend.{JDependPackageAnalyzer, JDependRunner, PackageFactory}

object PoorMansDIContainer {


  def buildAnalyzer(rootPackage: String, printer:Printer) = {

    val buildPackagesFn = buildPackageListProducerFn(rootPackage)
    val runAnalysisFn = buildRunAnalysisFn()
    val reporterFn = buildReporter()
    
    Application.buildApplicationFn(InputValidator.validate,buildPackagesFn, runAnalysisFn, reporterFn,printer)
  }

  def buildPackageListProducerFn(rootPackage: String): (String) => List[Package] = {
    val packageFactory = new PackageFactory(rootPackage)
    val packageListTransformer = packageFactory.buildPackageListFactory(PackageSorter.sortByName(_))
    JDependPackageAnalyzer.buildAnalyzerFn(JDependRunner.preparePackages, packageListTransformer)
  }

  def buildRunAnalysisFn(): AnalysisInput => List[AnalysisResult] = {
    val packageStructureBuilder = PackageStructureBuilder.createBuilder(PackageSorter.sortByName(_, _))
    val detectors = createDetectors(packageStructureBuilder)
    DetectorsRunner.buildDetectorsRunner(detectors)
  }

  private def createDetectors(buildPackageStructure: (List[Package], String) => Package) =
    List(
      LayerViolationDetector,
      ThirdPartyDetector,
      CycleDetector.buildInstance(buildPackageStructure),
      SDPViolationDetector,
      SAPViolationDetector,
      ACDDetector.buildInstance(buildPackageStructure),
      buildSubmodulesBlueprintViolationDetector(buildPackageStructure))


  private def buildSubmodulesBlueprintViolationDetector(buildPackageStructure: (List[Package], String) => Package) = {
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