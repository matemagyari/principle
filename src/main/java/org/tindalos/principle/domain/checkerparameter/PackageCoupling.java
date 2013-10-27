package org.tindalos.principle.domain.checkerparameter;

public class PackageCoupling {
	
	private ADP adp;
	private SDP sdp;
	private SAP sap;
	
	public SAP getSAP() {
		return sap;
	}

	public SDP getSDP() {
		return sdp;
	}
	
	public ADP getADP() {
		return adp;
	}

	public ADP getAdp() {
		return adp;
	}

	public void setAdp(ADP adp) {
		this.adp = adp;
	}

	public SDP getSdp() {
		return sdp;
	}

	public void setSdp(SDP sdp) {
		this.sdp = sdp;
	}

	public SAP getSap() {
		return sap;
	}

	public void setSap(SAP sap) {
		this.sap = sap;
	}
	
	
}
