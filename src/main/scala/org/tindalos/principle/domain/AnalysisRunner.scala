package org.tindalos.principle.domain

import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.utils.logging.TheLogger

trait AnalysisRunner {
  def run(input: AnalysisInput): List[AnalysisResult]
}

class AnalysisRunnerImpl(analyzers: List[Analyzer]) extends AnalysisRunner {

  override def run(input: AnalysisInput): List[AnalysisResult] =
    analyzers
      .filter(_.isEnabled(input.analysisPlan.constraints))
      .flatMap(runAnalyzer(input, _))


  private  def runAnalyzer(input: AnalysisInput, analyzer: Analyzer): Option[AnalysisResult] =
    try {
      TheLogger.info(analyzer + " is running.")
      Some(analyzer.analyze(input))
    } catch {
      case unwantedException: RuntimeException => TheLogger.error(unwantedException.getMessage)
        None
    }
}
