package org.tindalos.principle.infrastructure.plugin

import java.io.IOException

import org.apache.commons.lang3.Validate
import org.apache.maven.plugin.{AbstractMojo, MojoFailureException}
import org.apache.maven.plugins.annotations.{Mojo, Parameter}
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.core.logging.{ScalaLogger, TheLogger}
import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager

@Mojo(name = "check")
class DesignQualityCheckerMojo extends AbstractMojo {

  @Parameter(property = "check.configurationLocation")
  private var configurationLocation: String = null

  def execute(): Unit = {

    TheLogger.setLogger(new ScalaLogger() {
      override def info(msg: String) = {
        getLog().info(msg)
      }

      override def error(msg: String) = {
        getLog().info(msg)
      }
    })

    ReportsDirectoryManager.ensureReportsDirectoryExists()

    val (checks, rootPackage) = {
      val cl = if (configurationLocation == null) None else Some(configurationLocation)
      ChecksReader.readFromFile(cl)
    }

    val analyse = PoorMansDIContainer.buildAnalyzer(rootPackage, new LogPrinter(getLog()))
    try {
      val (success,msg) = analyse(new AnalysisPlan(checks, rootPackage))
      if (!success) throw new MojoFailureException("\nNumber of violations exceeds allowed limits!")
    } catch {
      case ex: IOException => getLog().error("/target/classes not found! " + ex.getMessage())
      case ex: InvalidConfigurationException => throw new MojoFailureException(ex.getMessage())
    }

  }
}
