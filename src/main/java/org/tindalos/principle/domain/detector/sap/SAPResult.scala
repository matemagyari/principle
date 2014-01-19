package org.tindalos.principle.domain.detector.sap

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.expectations.SAP

class SAPResult(val outlierPackages:List[Package], private val sapExpectation:SAP) extends CheckResult {
  
  	val expectationsFailed = outlierPackages.length > threshold
    val threshold = sapExpectation.violationsThreshold

}