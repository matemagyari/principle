package org.tindalos.principle.domain.expectations;

import org.tindalos.principle.domain.expectations.cumulativedependency.ACD;
import org.tindalos.principle.domain.expectations.cumulativedependency.NCCD;
import org.tindalos.principle.domain.expectations.cumulativedependency.RACD;

public class PackageCoupling {
	
	private ADP adp;
	private SDP sdp;
	private SAP sap;
	
	private ACD acd;
	private RACD racd;
	private NCCD nccd;
	
	public void setSAP(SAP sap) {
		this.sap = sap;
	}
	
	public SAP getSAP() {
		return sap;
	}

	public void setSDP(SDP sdp) {
		this.sdp = sdp;
	}
	
	public SDP getSDP() {
		return sdp;
	}
	
	public ADP getADP() {
		return adp;
	}

	public void setADP(ADP adp) {
		this.adp = adp;
	}

	public ACD getACD() {
		return acd;
	}

	public void setACD(ACD acd) {
		this.acd = acd;
	}
	public RACD getRACD() {
		return racd;
	}

	public void setRACD(RACD racd) {
		this.racd = racd;
	}

	public NCCD getNCCD() {
		return nccd;
	}

	public void setNCCD(NCCD nccd) {
		this.nccd = nccd;
	}
	
	
}
