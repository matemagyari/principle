package org.tindalos.principle.domain.analyzers.sap

import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.packages.PackageWithMetrics

import scala.collection.JavaConverters._

object SAPViolationAnalyzer extends Analyzer {

  private def toScalaOption[T](javaOptional: java.util.Optional[T]): Option[T] = {
    if (javaOptional.isPresent) Some(javaOptional.get()) else None
  }

  override def analyze(checkInput: AnalysisInput) = {
    val sapExpectation = checkInput.packageCouplingExpectations().flatMap(pc => toScalaOption(pc.sap())).get
    val maxDistance = sapExpectation.maxDistance

    val outlierPackages = removeRootPackageIfEmpty(checkInput.packages).filter(_.distance > maxDistance)

    new SAPResult(outlierPackages.asInstanceOf[List[PackageWithMetrics]].asJava, sapExpectation)
  }

  private def removeRootPackageIfEmpty(packages: List[Package]) = {
    val metrics = packages.head.getMetrics()
    if (metrics.abstractness == 0 && metrics.instability == 0) packages.tail
    else packages
  }

  override def isEnabled(expectations: Constraints) =
    expectations.packageCoupling().isPresent && expectations.packageCoupling().get().sap().isPresent

}