package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration

class DesignQualityCheckService(val packageAnalyzer:PackageAnalyzer
								, val designQualityDetectorsRunner: DesignQualityDetectorsRunner) {
  
    def analyze(parameters: DesignQualityCheckConfiguration) = {
    	println("we are using Scala !!!")
        val packages = packageAnalyzer.analyze(parameters)
        designQualityDetectorsRunner.execute(packages, parameters)
    }

}