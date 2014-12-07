package org.tindalos.principle.domain.detector.sap

import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.expectations.SAP

class SAPResult(val outlierPackages:List[Package], private val sapExpectation:SAP) extends AnalysisResult {
  
	val threshold = sapExpectation.violationsThreshold
  	override def expectationsFailed() = outlierPackages.length > threshold

}