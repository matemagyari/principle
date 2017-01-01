package org.tindalos.principle.domain.agents.sdp

import org.tindalos.principle.domain.core.{Package, PackageReference}
import org.tindalos.principle.domain.agentscore.{AnalysisInput, Agent}
import org.tindalos.principle.domain.expectations.{Checks, PackageCoupling}

object SDPViolationAgent extends Agent {

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

  override def isWanted(expectations: Checks) = expectations.packageCoupling match {
    case packageCoupling: PackageCoupling => packageCoupling.sdp != null
    case null => false
  }

}