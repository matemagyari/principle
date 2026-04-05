file://<WORKSPACE>/src/main/scala/org/tindalos/principle/app/ApplicationModule.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -scala/collection/JavaConverters.nodes.
	 -scala/collection/JavaConverters.nodes#
	 -scala/collection/JavaConverters.nodes().
	 -nodes.
	 -nodes#
	 -nodes().
	 -scala/Predef.nodes.
	 -scala/Predef.nodes#
	 -scala/Predef.nodes().
offset: 1303
uri: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/app/ApplicationModule.scala
text:
```scala
package org.tindalos.principle.app

import org.tindalos.principle.domain.{AnalysisInput, AnalysisResult, AnalysisRunner}
import org.tindalos.principle.domain.core.{AnalysisPlan, Package}
import org.tindalos.principle.domain.core.Node
import org.tindalos.principle.domain.core.packages.PackageWithMetrics
import org.tindalos.principle.app.reporters.AnalysisResultsReporter
import org.tindalos.principle.infrastructure.{JDependBasedPackageListBuilder, PackageListBuilder}
import scala.collection.JavaConverters._

/*
This is the app entry point. Side effects can happen only here in this layer, underneath the code must be pure.
 */
object ApplicationModule {

  def buildApplicationFn(inputValidator: AnalysisPlanValidator,
                         packageListBuilder: PackageListBuilder,
                         getNodes: String => Set[Node],
                         analysisRunner: AnalysisRunner,
                         analysisResultsReporter: AnalysisResultsReporter,
                         printer: Printer): AnalysisPlan => ValidationResult =

    (analysisPlan: AnalysisPlan) => {

      val validationResult = inputValidator.validate(analysisPlan)

      if (validationResult.success) {

        val packages = packageListBuilder.build()
        val nodes@@ = getNodes(analysisPlan.basePackage)
        val packageInputs = packages.asScala.map(p => p: PackageWithMetrics).toList

        val analysisResults = analysisRunner.run(new AnalysisInput(packageInputs.asJava, nodes.asJava, analysisPlan))

        printer.printInfo(analysisResultsReporter.summary(analysisResults))

        val success = !analysisResults.stream().anyMatch(_.constraintViolated())
        new ValidationResult(success, if (success) "" else "Expectations failed")

      } else validationResult

    }

}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 