package org.tindalos.principle.domain.detector.submodulesblueprint;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SubmoduleId {
    
    private final String name;

    public SubmoduleId(String name) {
        this.name = name;
    }
    
    public String value() {
		return name;
	}

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SubmoduleId)) {
            return false;
        }
        SubmoduleId castOther = (SubmoduleId) other;

        return name.equals(castOther.name);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
