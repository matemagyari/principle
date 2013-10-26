package org.tindalos.principle.domain.detector.sdp;

import org.tindalos.principle.domain.core.Package;
public class SDPViolation {
	
	private final Package depender;
	private final Package dependee;
	
	public SDPViolation(Package depender, Package dependee) {
		this.depender = depender;
		this.dependee = dependee;
	}
	
	public Package getDepender() {
		return depender;
	}
	
	public Package getDependee() {
		return dependee;
	}

	@Override
	public String toString() {
		return "SDPViolation [depender=" + depender + ", dependee=" + dependee + "]";
	}

}
