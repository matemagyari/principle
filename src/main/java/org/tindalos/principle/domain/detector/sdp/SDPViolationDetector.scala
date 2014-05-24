package org.tindalos.principle.domain.detector.sdp

import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.core.PackageReference
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.expectations.{PackageCoupling, DesignQualityExpectations}

class SDPViolationDetector extends Detector {

  override def analyze(checkInput: CheckInput) = {

    val references: Map[PackageReference, Package] = checkInput.packages.map(aPackage => (aPackage.reference -> aPackage)).toMap
    val sdpViolations = for (aPackage <- checkInput.packages)
    yield
      aPackage.getOwnPackageReferences()
        .map(x => references.get(x).get)
        .filter(_.instability > aPackage.instability)
        .map(new SDPViolation(aPackage, _))


    new SDPResult(sdpViolations.flatten, checkInput.getPackageCouplingExpectations().sdp)
  }

  override def isWanted(expectations: DesignQualityExpectations) = expectations.packageCoupling match {
    case packageCoupling: PackageCoupling => packageCoupling.sdp != null
    case null => false
  }

}