package org.tindalos.principle.domain.detector.sdp

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.expectations.SDP

class SDPResult(val violations:List[SDPViolation], private val expectation:SDP) extends CheckResult {
  
  val threshold = expectation.violationsThreshold
  override def expectationsFailed() = violations.length > threshold
  
  

}