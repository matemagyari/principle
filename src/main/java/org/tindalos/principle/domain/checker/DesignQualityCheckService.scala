package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import scala.collection.JavaConversions._
import org.tindalos.principle.domain.core.Package

class DesignQualityCheckService(val packageAnalyzer:PackageAnalyzer
								, val designQualityDetectorsRunner: DesignQualityDetectorsRunner) {
  
    def analyze(parameters: DesignQualityCheckConfiguration) = {
        val packages = packageAnalyzer.analyze(parameters)
        designQualityDetectorsRunner.detectorResults(packages, parameters)
    }

}