package org.tindalos.principle.domain.agentscore

import org.tindalos.principle.domain.constraints.Constraints

trait Analyzer {

  def analyze(checkInput: AnalysisInput): AnalysisResult
  def isEnabled(constraints: Constraints): Boolean
  
  override def toString() = this.getClass().getSimpleName()

}