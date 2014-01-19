package org.tindalos.principle.domain.detector.sdp

import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.detector.layering.LayerReference
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult
import org.tindalos.principle.domain.expectations.PackageCoupling
import org.tindalos.principle.domain.core.PackageReference
import org.tindalos.principle.domain.core.Package
import scala.collection.mutable.ListBuffer

class SDPViolationDetector extends Detector {

  override def analyze(checkInput: CheckInput) = {
    
     val sdpViolations = ListBuffer[SDPViolation]()
     val references:Map[PackageReference, Package] = checkInput.packages.map(aPackage => (aPackage.reference -> aPackage)).toMap
     for (aPackage <-checkInput.packages) {
       val violationsForPackage = aPackage.getOwnPackageReferences()
    		   	.map(x => references.get(x).get)
       			.filter(_.instability > aPackage.instability)
       			.map(new SDPViolation(aPackage, _))
       sdpViolations.++=(violationsForPackage)			
     }
     
     new SDPResult(sdpViolations.toList, checkInput.getPackageCouplingExpectations().getSDP())
  }

  override def isWanted(expectations: DesignQualityExpectations) = expectations.getPackageCoupling() match {
    case packageCoupling: PackageCoupling => packageCoupling.getSDP() != null
    case null => false
  }

}