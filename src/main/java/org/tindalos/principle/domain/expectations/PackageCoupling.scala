package org.tindalos.principle.domain.expectations

import org.tindalos.principle.domain.expectations.cumulativedependency.NCCD
import org.tindalos.principle.domain.expectations.cumulativedependency.ACD
import org.tindalos.principle.domain.expectations.cumulativedependency.RACD

class PackageCoupling {
  
  	var  adp:ADP = null
	var  sdp:SDP = null
	var  sap:SAP = null
	
	var  acd:ACD = null
	var  racd:RACD = null
	var  nccd:NCCD = null

}