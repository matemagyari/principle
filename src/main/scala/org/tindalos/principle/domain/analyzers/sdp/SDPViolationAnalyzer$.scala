package org.tindalos.principle.domain.analyzers.sdp

import org.tindalos.principle.domain.core.{Package, PackageReference}
import org.tindalos.principle.domain.agentscore.{AnalysisInput, Analyzer}
import org.tindalos.principle.domain.constraints.Constraints

object SDPViolationAnalyzer extends Analyzer {

  private def toScalaOption[T](javaOptional: java.util.Optional[T]): Option[T] = {
    if (javaOptional.isPresent) Some(javaOptional.get()) else None
  }

  override def analyze(checkInput: AnalysisInput) = {

    val references = checkInput.packages.map(aPackage => (aPackage.reference -> aPackage)).toMap
    val sdpViolations = for (aPackage <- checkInput.packages)
    yield
      aPackage.getOwnPackageReferences()
        .map(x => references.get(x).get)
        .filter(_.instability > aPackage.instability)
        .map(new SDPViolation(aPackage, _))


    new SDPResult(sdpViolations.flatten, checkInput.packageCouplingExpectations().flatMap(pc => toScalaOption(pc.sdp())).get)
  }

  override def isEnabled(expectations: Constraints) =
    expectations.packageCoupling().isPresent && expectations.packageCoupling().get().sdp().isPresent

}