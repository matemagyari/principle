package org.tindalos.principle.domain.agentscore

trait AnalysisResult {
  def constraintViolated():Boolean
}