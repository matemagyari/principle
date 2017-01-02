package org.tindalos.principle

import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.core.logging.{ScalaLogger, TheLogger}
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.domain.resultprocessing.reporter.Printer
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer

object ManualPrincipleRunner extends App {


  val printer = new ConsolePrinter()

  TheLogger.setLogger(new ScalaLogger() {
    override def info(msg: String) = {
      printer.printInfo(msg)
    }

    override def error(msg: String) = {
      printer.printWarning(msg)
    }
  })

  val basePackage = "org.tindalos.principle"
  val runAnalysis = PoorMansDIContainer.buildAnalyzer(basePackage, printer)
  val checks = Checks(packageCoupling = Some(PackageCoupling(grouping = Grouping())))

  runAnalysis(new AnalysisPlan(checks, basePackage))

//  {
//    val checks = new Checks()
//
//    //checks.layering = layering()
//    checks.packageCoupling = packageCoupling()
//    //checks.setSubmodulesBlueprint(submodulesBlueprint())
//    checks
//  }

  //private val submodulesDefinitionLocation = "src/main/resources/principle_blueprint.yaml"
  //private val submodulesBlueprint = new SubmodulesBlueprint(submodulesDefinitionLocation, 0)

  private def layering() = new Layering(List("infrastructure", "app", "domain"), 0)

  class ConsolePrinter extends Printer {

    def printWarning(text: String) = {
      System.err.println(text)
    }

    def printInfo(text: String) = {
      System.out.println(text)
    }
  }

}
