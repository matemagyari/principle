package org.tindalos.principle.domain.detector.acd

import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.detector.adp.PackageStructureBuilder
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.tindalos.principle.domain.expectations.PackageCoupling

class ACDDetector(private val packageStructureBuilder: PackageStructureBuilder) extends Detector {

  override def analyze(checkInput: CheckInput) = {

    val basePackage = packageStructureBuilder.build(checkInput.packages, checkInput.designQualityCheckConfiguration.basePackage)

    val referenceMap = basePackage.toMap()

    val relevantPackages =
      if (basePackage.isIsolated()) checkInput.packages.filterNot(_ equals basePackage)
      else checkInput.packages

    val cumulatedComponentDependency = relevantPackages
      .foldLeft(0)((acc, aPackage) => acc + aPackage.cumulatedDependencies(referenceMap).size + 1)

    new ACDResult(cumulatedComponentDependency, relevantPackages.length, checkInput.getPackageCouplingExpectations())
  }

  override def isWanted(expectations: DesignQualityExpectations) = expectations.packageCoupling match {
    case packageCoupling: PackageCoupling =>
      (packageCoupling.getACD() != null) ||
        (packageCoupling.getRACD() != null) ||
        (packageCoupling.getNCCD() != null)
    case null => false
  }

}