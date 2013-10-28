package org.tindalos.principle.domain.core.checkerparameter;

public class PackageCoupling {
	
	private ADP adp;
	private SDP sdp;
	private SAP sap;
	
	private ACD acd;
	
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
	
	
}
