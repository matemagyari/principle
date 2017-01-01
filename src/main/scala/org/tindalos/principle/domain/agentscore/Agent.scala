package org.tindalos.principle.domain.agentscore

import org.tindalos.principle.domain.expectations.Checks

trait Agent {

  def analyze(checkInput: AnalysisInput): AnalysisResult
  def isWanted(designQualityExpectations: Checks): Boolean
  
  override def toString() = this.getClass().getSimpleName()

}