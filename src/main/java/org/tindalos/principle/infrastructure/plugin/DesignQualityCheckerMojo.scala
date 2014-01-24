package org.tindalos.principle.infrastructure.plugin

import org.apache.commons.lang3.Validate
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.tindalos.principle.app.service.Application
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.core.logging.ScalaLogger
import org.tindalos.principle.domain.core.logging.TheLogger
import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.service.jdepend.ClassesToAnalyzeNotFoundException

@Mojo(name = "check")
class DesignQualityCheckerMojo extends AbstractMojo {

  @Parameter(property = "check.basePackage", defaultValue = "")
  private var basePackage = ""

  @Parameter(property = "check.checks")
  private var checks: Checks = null

  def execute(): Unit = {

    Validate.notNull(checks, "Missing <checks> tag!")
    Validate.notBlank(basePackage, "Missing <basePackage> tag!")

    TheLogger.setLogger(new ScalaLogger() {
      override def info(msg: String) = {
        getLog().info(msg)
      }

      override def error(msg: String) = {
        getLog().info(msg)
      }
    })

    val application = PoorMansDIContainer.getApplication(basePackage)
    try {
      application.doIt(new DesignQualityCheckConfiguration(checks, basePackage), new LogPrinter(getLog()))
    } catch {
      case ex: ClassesToAnalyzeNotFoundException => getLog().warn(ex.getMessage())
      case ex: ThresholdTrespassedException => throw new MojoFailureException("\nNumber of violations exceeds allowed limits!")
      case ex: InvalidConfigurationException => throw new MojoFailureException(ex.getMessage())
    }

  }

}
