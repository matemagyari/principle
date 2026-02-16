package org.tindalos.principle.domain

import org.tindalos.principle.domain.agentscore.{AnalysisInput, Analyzer}
import org.tindalos.principle.domain.core.logging.TheLogger

trait AnalysisRunner {
  def run(input: AnalysisInput): List[AnalysisResult]
}

class AnalysisRunnerImpl(analyzers: List[Analyzer]) extends AnalysisRunner {

  override def run(input: AnalysisInput): List[AnalysisResult] =
    analyzers
      .filter(_.isEnabled(input.analysisPlan.expectations))
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
