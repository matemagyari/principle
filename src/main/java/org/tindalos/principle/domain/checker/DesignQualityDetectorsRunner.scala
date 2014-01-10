package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.core.Package
import java.util.List
import java.util.ArrayList

class DesignQualityDetectorsRunner(val detectors:List[Detector]) {
  
  def this(theDetectors:Detector*) = this(new ArrayList(theDetectors))
  
  def execute(packages:List[Package], designQualityCheckConfiguration:DesignQualityCheckConfiguration) = {
    val checkInput = new CheckInput(packages, designQualityCheckConfiguration);
  } 

}