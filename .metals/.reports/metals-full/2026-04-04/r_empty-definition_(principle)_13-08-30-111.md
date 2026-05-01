error id: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/app/ApplicationModule.scala:AnalysisPlan
file://<WORKSPACE>/src/main/scala/org/tindalos/principle/app/ApplicationModule.scala
empty definition using pc, found symbol in pc: 
semanticdb not found

found definition using fallback; symbol AnalysisPlan
offset: 994
uri: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/app/ApplicationModule.scala
text:
```scala
package org.tindalos.guardrails.app

import org.tindalos.guardrails.domain.{AnalysisInput, AnalysisResult, AnalysisRunner}
import org.tindalos.guardrails.domain.core.{AnalysisPlan, Package}
import org.tindalos.guardrails.domain.core.packages.PackageWithMetrics
import org.tindalos.guardrails.app.reporters.AnalysisResultsReporter
import org.tindalos.guardrails.infrastructure.{JDependBasedPackageListBuilder, PackageListBuilder}
import scala.collection.JavaConverters._

/*
This is the app entry point. Side effects can happen only here in this layer, underneath the code must be pure.
 */
object ApplicationModule {

  def buildApplicationFn(inputValidator: AnalysisPlanValidator,
                         packageListBuilder: PackageListBuilder,
                         nodeBuilder: NodeBuilder,
                         analysisRunner: AnalysisRunner,
                         analysisResultsReporter: AnalysisResultsReporter,
                         printer: Printer): Analysi@@sPlan => ValidationResult =

    (analysisPlan: AnalysisPlan) => {

      val validationResult = inputValidator.validate(analysisPlan)

      if (validationResult.success) {

        val packages = packageListBuilder.build()
        val nodes = nodeBuilder.build(analysisPlan.basePackage)
        val packageInputs = packages.asScala.map(p => p: PackageWithMetrics).toList

        val analysisResults = analysisRunner.run(new AnalysisInput(packageInputs.asJava, nodes, analysisPlan))

        printer.printInfo(analysisResultsReporter.summary(analysisResults))

        val success = !analysisResults.stream().anyMatch(_.constraintViolated())
        new ValidationResult(success, if (success) "" else "Expectations failed")

      } else validationResult

    }

}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 