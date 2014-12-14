package org.tindalos.principle.domain.coredetector

import org.tindalos.principle.domain.expectations.Expectations

trait Detector {

  def analyze(checkInput: AnalysisInput): AnalysisResult
  def isWanted(designQualityExpectations: Expectations): Boolean
  
  override def toString() = this.getClass().getSimpleName()

}