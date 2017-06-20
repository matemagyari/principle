package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework.JavaPackage
import org.tindalos.principle.domain.core.{AnalysisPlan, Package}

import scala.collection.immutable.Seq

object JDependPackageAnalyzer {

  def buildAnalyzerFn(produceJavaPackageList: (String, Boolean) => Seq[JavaPackage]
                    , transform: (String, Seq[JavaPackage]) => Seq[Package]) =

    (rootPackage: String) => {
      val analyzedPackages = produceJavaPackageList(rootPackage,true)
      transform(rootPackage, analyzedPackages)
    }

}