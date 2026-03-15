package org.tindalos.principle.domain.analyzers.sdp

import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.constraints.Constraints

import scala.collection.JavaConverters._

object SDPViolationAnalyzer extends Analyzer {

  override def analyze(checkInput: AnalysisInput) = {

    val references = checkInput.packages.map(aPackage => (aPackage.reference -> aPackage)).toMap
    val sdpViolations = for (aPackage <- checkInput.packages)
    yield
      aPackage.getOwnPackageReferences()
        .asScala
        .map(x => references.get(x).get)
        .filter(_.getMetrics().instability > aPackage.getMetrics().instability)
        .map(new SDPViolation(aPackage, _))


    new SDPResult(sdpViolations.flatten.asJava, checkInput.packageCouplingExpectations().flatMap(_.sdp()).get)
  }

  override def isEnabled(expectations: Constraints) =
    expectations.packageCoupling().isPresent && expectations.packageCoupling().get().sdp().isPresent

}