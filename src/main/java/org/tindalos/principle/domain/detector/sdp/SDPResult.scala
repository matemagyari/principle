package org.tindalos.principle.domain.detector.sdp

import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.expectations.SDP

class SDPResult(val violations:List[SDPViolation], private val expectation:SDP) extends AnalysisResult {
  
  val threshold = expectation.violationsThreshold
  override def expectationsFailed() = violations.length > threshold
  
  

}