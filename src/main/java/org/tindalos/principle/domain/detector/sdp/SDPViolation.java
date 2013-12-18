package org.tindalos.principle.domain.detector.sdp;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
    public boolean equals(Object other) {
        if (!(other instanceof SDPViolation)) {
            return false;
        }
        SDPViolation castOther = (SDPViolation) other;
        return new EqualsBuilder()
                    .append(depender, castOther.depender)
                    .append(dependee, castOther.dependee)
                    .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                        .append(depender)
                        .append(dependee)
                        .hashCode();
    }
	@Override
	public String toString() {
		return "SDPViolation [depender=" + depender + " [ " + depender.instability()+"], dependee=" + dependee + " [ " + dependee.instability()+"]";
	}

}
