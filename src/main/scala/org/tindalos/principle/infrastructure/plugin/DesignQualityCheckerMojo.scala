package org.tindalos.principle.infrastructure.plugin

import java.io.IOException
import org.apache.maven.plugin.{AbstractMojo, MojoFailureException}
import org.apache.maven.plugins.annotations.{Mojo, Parameter}
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.constraints.exception.InvalidConfigurationException
import org.tindalos.principle.infrastructure.ConstraintsReader
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager
import org.tindalos.principle.utils.logging.{SimpleLogger, TheLogger}

@Mojo(name = "check")
class DesignQualityCheckerMojo extends AbstractMojo {

  @Parameter(property = "check.location")
  private var location: String = null

  def execute(): Unit = {

    TheLogger.setLogger(new SimpleLogger() {
      override def info(msg: String) = {
        getLog().info(msg)
      }

      override def error(msg: String) = {
        getLog().info(msg)
      }
    })

    ReportsDirectoryManager.ensureReportsDirectoryExists()

    val plan = ConstraintsReader.readFromFile(java.util.Optional.ofNullable(location))

    val analyser = PoorMansDIContainer.buildAnalyzer(plan.basePackage(), new LogPrinter(getLog()))
    try {
      val result = analyser.analyze(plan)
      if (!result.success) throw new MojoFailureException("\nNumber of violations exceeds allowed limits!")
    } catch {
      case ex: IOException => getLog().error("/target/classes not found! " + ex.getMessage())
      case ex: InvalidConfigurationException => throw new MojoFailureException(ex.getMessage())
      case ex: Exception ⇒ throw new MojoFailureException("Unexpected error", ex)
    }

  }
}
