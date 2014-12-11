package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework.JavaPackage
import org.tindalos.principle.domain.core.{AnalysisInput, Package}

object JDependPackageAnalyzer {

  def buildAnalyzer(jDependRunner: (String, Boolean) => List[JavaPackage]
                    , packageListFactory: List[JavaPackage] => List[Package]) =

    (configuration: AnalysisInput) => {
      val analyzedPackages = jDependRunner(configuration.basePackage,true)
      packageListFactory(analyzedPackages)
    }

}