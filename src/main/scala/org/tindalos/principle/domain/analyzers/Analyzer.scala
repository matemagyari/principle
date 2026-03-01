package org.tindalos.principle.domain.analyzers

import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.{AnalysisInput, AnalysisResult}

trait Analyzer {

  def analyze(checkInput: AnalysisInput): AnalysisResult
  def isEnabled(constraints: Constraints): Boolean
  
  override def toString() = this.getClass().getSimpleName()

}