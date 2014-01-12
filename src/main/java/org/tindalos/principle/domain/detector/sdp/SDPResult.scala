package org.tindalos.principle.domain.detector.sdp

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.expectations.SDP
import org.tindalos.principle.domain.core.ListConverter

class SDPResult(val violations:List[SDPViolation], private val expectation:SDP) extends CheckResult {
  
  override def expectationsFailed() = violations.length > expectation.getViolationsThreshold()
  
  val threshold = expectation.getViolationsThreshold()
  
  def getViolationsAsJavaList() = ListConverter.convert(violations)

}