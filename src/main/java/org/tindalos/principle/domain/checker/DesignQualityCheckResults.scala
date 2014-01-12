package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.core.ListConverter

class DesignQualityCheckResults(val checkResults: List[CheckResult]) {

  def getResult[T <: CheckResult](clazz: Class[T]) = checkResults.find(_.getClass() == clazz)

  def failExpectations() = checkResults.exists(_.expectationsFailed)
  
  def resultList() = ListConverter.convert(checkResults)

}