package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework.JavaPackage
import org.tindalos.principle.domain.core.{AnalysisPlan, Package}

object JDependPackageAnalyzer {

  def buildAnalyzerFn(produceJavaPackageList: (String, Boolean) => List[JavaPackage]
                    , transform: (String, List[JavaPackage]) => List[Package]) =

    (rootPackage: String) => {
      val analyzedPackages = produceJavaPackageList(rootPackage,true)
      transform(rootPackage, analyzedPackages)
    }

}