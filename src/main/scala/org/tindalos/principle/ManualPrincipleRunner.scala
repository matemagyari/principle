package org.tindalos.principle

import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.{ConsolePrinter, ConstraintsReader}
import org.tindalos.principle.utils.logging.{SimpleLogger, TheLogger}

import java.util.Optional

object ManualPrincipleRunner extends App {


  val printer = new ConsolePrinter()

  TheLogger.setLogger(new SimpleLogger() {
    override def info(msg: String) = {
      printer.printInfo(msg)
    }

    override def error(msg: String) = {
      printer.printWarning(msg)
    }
  })

  val plan: AnalysisPlan = ConstraintsReader.readFromFile(Optional.of("/Users/mate.magyari/private/PrivateProjects/principle/principle.yml"))
  val runAnalysis = PoorMansDIContainer.buildAnalyzer(plan.basePackage(), printer)
  runAnalysis(plan)

}
