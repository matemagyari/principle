package org.tindalos.principle.domain.detector.layering;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LayerReference {
    
    private String referer;
    private String referee;
    
    public LayerReference(String referer, String referee) {
        this.referer = referer;
        this.referee = referee;
    }

    @Override
    public String toString() {
        return referer + " -> " + referee;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LayerReference)) {
            return false;
        }
        LayerReference castOther = (LayerReference) other;
        return new EqualsBuilder()
                    .append(referer, castOther.referer)
                    .append(referee, castOther.referee)
                    .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                        .append(referer)
                        .append(referee)
                        .hashCode();
    }
}