package org.tindalos.principle.domain

import org.tindalos.principle.domain.agentscore.AnalysisInput

trait AnalysisRunner {

  def run(analysisInput: AnalysisInput): List[AnalysisResult]

}
