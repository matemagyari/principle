package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework.JavaPackage
import org.tindalos.principle.domain.core.{DesignQualityCheckConfiguration, Package}

object JDependPackageAnalyzer {

  def buildAnalyzer(jDependRunner: (String, Boolean) => List[JavaPackage]
                    , packageListFactory: List[JavaPackage] => List[Package]) =

    (configuration: DesignQualityCheckConfiguration) => {
      val analyzedPackages = jDependRunner(configuration.basePackage,true)
      packageListFactory(analyzedPackages)
    }

}