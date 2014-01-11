package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.core.Package

trait PackageAnalyzer {
  
  def analyze(configuration: DesignQualityCheckConfiguration): List[Package]

}