package org.tindalos.principle.domain.agentscore

import org.tindalos.principle.domain.constraints.Constraints

trait Agent {

  def analyze(checkInput: AnalysisInput): AnalysisResult
  def isWanted(designQualityExpectations: Constraints): Boolean
  
  override def toString() = this.getClass().getSimpleName()

}