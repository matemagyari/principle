package org.tindalos.principle.infrastructure.plugin

import org.apache.commons.lang3.Validate
import org.apache.maven.plugin.{AbstractMojo, MojoFailureException}
import org.apache.maven.plugins.annotations.{Mojo, Parameter}
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.core.logging.{ScalaLogger, TheLogger}
import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException
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
      val (success,msg) = application(new DesignQualityCheckConfiguration(checks, basePackage), new LogPrinter(getLog()))
      if (!success) throw new MojoFailureException("\nNumber of violations exceeds allowed limits!")
    } catch {
      case ex: ClassesToAnalyzeNotFoundException => getLog().warn(ex.getMessage())
      case ex: InvalidConfigurationException => throw new MojoFailureException(ex.getMessage())
    }

  }

}
