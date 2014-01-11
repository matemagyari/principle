package org.tindalos.principle.domain.coredetector

import org.tindalos.principle.domain.expectations.DesignQualityExpectations

trait Detector {

  def analyze(checkInput: CheckInput): CheckResult
  def isWanted(designQualityExpectations: DesignQualityExpectations): Boolean

}