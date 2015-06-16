package org.tindalos.principle.domain.agentscore

import org.tindalos.principle.domain.expectations.Expectations

trait Agent {

  def analyze(checkInput: AnalysisInput): AnalysisResult
  def isWanted(designQualityExpectations: Expectations): Boolean
  
  override def toString() = this.getClass().getSimpleName()

}