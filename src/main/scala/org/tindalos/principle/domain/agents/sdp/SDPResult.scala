package org.tindalos.principle.domain.agents.sdp

import org.tindalos.principle.domain.agentscore.AnalysisResult
import org.tindalos.principle.domain.expectations.SDP

class SDPResult(val violations:List[SDPViolation], expectation:SDP) extends AnalysisResult {
  
  val threshold = expectation.violationsThreshold
  override def expectationsFailed() = violations.length > threshold
  
  

}