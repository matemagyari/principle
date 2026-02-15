package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.logging.TheLogger
import org.tindalos.principle.domain.agentscore.{Analyzer, AnalysisInput, AnalysisResult}

object AgentsRunner {

  def buildAgentsRunner(agents: List[Analyzer]) =

    (input: AnalysisInput) => {

      val results = for {
        agent <- agents if agent.isWanted(input.analysisPlan.expectations)
      } yield runDetector(input, agent)

      results.flatten
    }

  private def runDetector(input: AnalysisInput, agent: Analyzer): Option[AnalysisResult] =
    try {
      TheLogger.info(agent + " is running.")
      Some(agent.analyze(input))
    } catch {
      case unwantedException: RuntimeException => TheLogger.error(unwantedException.getMessage)
        None
    }

}