package org.tindalos.principle.app

import org.tindalos.principle.domain.{AnalysisInput, AnalysisResult, AnalysisRunner}
import org.tindalos.principle.domain.core.{AnalysisPlan, Package}
import org.tindalos.principle.domain.analyzers.structure.Node
import org.tindalos.principle.domain.core.packages.PackageWithMetrics
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter
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
        val nodes = getNodes(analysisPlan.basePackage)
        val packageInputs = packages.map(p => p: PackageWithMetrics)

        val analysisResults = analysisRunner.run(new AnalysisInput(packageInputs.asJava, nodes.asJava, analysisPlan))

        printer.printInfo(analysisResultsReporter.summary(analysisResults))

        val success = !analysisResults.stream().anyMatch(_.constraintViolated())
        new ValidationResult(success, if (success) "" else "Expectations failed")

      } else validationResult

    }

}