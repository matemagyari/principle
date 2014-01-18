package org.tindalos.principle.domain.detector.sap

import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.tindalos.principle.domain.expectations.PackageCoupling
import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.core.Package

class SAPViolationDetector extends Detector {

  override def analyze(checkInput: CheckInput) = {
    val sapExpectation = checkInput.getPackageCouplingExpectations().getSAP()
    val maxDistance = sapExpectation.getMaxDistance()

    val outlierPackages = removeRootPackageIfEmpty(checkInput.packages).filter(_.distance > maxDistance)

    new SAPResult(outlierPackages, sapExpectation)
  }

  private def removeRootPackageIfEmpty(packages: List[Package]) = {
    val metrics = packages.head.getMetrics()
    if (metrics.abstractness == 0 && metrics.instability == 0) packages.tail
    else packages
  }

  override def isWanted(expectations: DesignQualityExpectations) = expectations.getPackageCoupling() match {
    case packageCoupling: PackageCoupling => packageCoupling.getSAP() != null
    case null => false
  }

}