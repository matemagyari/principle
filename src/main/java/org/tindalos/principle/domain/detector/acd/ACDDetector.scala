package org.tindalos.principle.domain.detector.acd

import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.coredetector.{PackagesAndExpectations, Detector}
import org.tindalos.principle.domain.expectations.{PackageCoupling, Expectations}

object ACDDetector {
  def buildInstance(packageStructureBuilder: (List[Package], String) => Package) = new Detector {

    override def analyze(checkInput: PackagesAndExpectations) = {

      val basePackage = packageStructureBuilder(checkInput.packages, checkInput.expectationsConfig.basePackage)

      val referenceMap = basePackage.toMap()

      val relevantPackages =
        if (basePackage.isIsolated()) checkInput.packages.filterNot(_ equals basePackage)
        else checkInput.packages

      val cumulatedComponentDependency = relevantPackages
        .foldLeft(0)((acc, aPackage) => acc + aPackage.cumulatedDependencies(referenceMap).size + 1)

      new ACDResult(cumulatedComponentDependency, relevantPackages.length, checkInput.packageCouplingExpectations())
    }

    override def isWanted(expectations: Expectations) = expectations.packageCoupling match {
      case packageCoupling: PackageCoupling =>
        (packageCoupling.acd != null) ||
          (packageCoupling.racd != null) ||
          (packageCoupling.nccd != null)
      case null => false
    }

  }
}