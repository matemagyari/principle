package org.tindalos.principle.domain.coredetector

trait ViolationsReporter[T <: CheckResult] {

  def report(result: T): String
  def getType(): Class[T]

}