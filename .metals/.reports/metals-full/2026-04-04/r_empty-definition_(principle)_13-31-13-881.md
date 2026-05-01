file://<WORKSPACE>/src/main/scala/org/tindalos/principle/infrastructure/plugin/DesignQualityCheckerMojo.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -analyser/analyse.
	 -analyser/analyse#
	 -analyser/analyse().
	 -scala/Predef.analyser.analyse.
	 -scala/Predef.analyser.analyse#
	 -scala/Predef.analyser.analyse().
offset: 1320
uri: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/infrastructure/plugin/DesignQualityCheckerMojo.scala
text:
```scala
package org.tindalos.guardrails.infrastructure.plugin

import java.io.IOException
import org.apache.maven.plugin.{AbstractMojo, MojoFailureException}
import org.apache.maven.plugins.annotations.{Mojo, Parameter}
import org.tindalos.guardrails.domain.plan.AnalysisPlan
import org.tindalos.guardrails.domain.constraints.exception.InvalidConfigurationException
import org.tindalos.guardrails.infrastructure.core.ConstraintsReader
import org.tindalos.guardrails.infrastructure.di.PoorMansDIContainer
import org.tindalos.guardrails.infrastructure.reporters.ReportsDirectoryManager
import org.tindalos.guardrails.utils.logging.{SimpleLogger, TheLogger}

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
      val result = analyser.analyse@@(plan)
      if (!result.success) throw new MojoFailureException("\nNumber of violations exceeds allowed limits!")
    } catch {
      case ex: IOException => getLog().error("/target/classes not found! " + ex.getMessage())
      case ex: InvalidConfigurationException => throw new MojoFailureException(ex.getMessage())
      case ex: Exception ⇒ throw new MojoFailureException("Unexpected error", ex)
    }

  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 