package org.tindalos.principle.domain.detector.sdp

import org.tindalos.principle.domain.core.{Package, PackageReference}
import org.tindalos.principle.domain.coredetector.{AnalysisInput, Detector}
import org.tindalos.principle.domain.expectations.{Expectations, PackageCoupling}

object SDPViolationDetector extends Detector {

  override def analyze(checkInput: AnalysisInput) = {

    val references = checkInput.packages.map(aPackage => (aPackage.reference -> aPackage)).toMap
    val sdpViolations = for (aPackage <- checkInput.packages)
    yield
      aPackage.getOwnPackageReferences()
        .map(x => references.get(x).get)
        .filter(_.instability > aPackage.instability)
        .map(new SDPViolation(aPackage, _))


    new SDPResult(sdpViolations.flatten, checkInput.packageCouplingExpectations().sdp)
  }

  override def isWanted(expectations: Expectations) = expectations.packageCoupling match {
    case packageCoupling: PackageCoupling => packageCoupling.sdp != null
    case null => false
  }

}