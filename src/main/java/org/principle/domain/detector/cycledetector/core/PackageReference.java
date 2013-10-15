package org.principle.domain.detector.cycledetector.core;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PackageReference implements Comparable<PackageReference> {

    private final String name;

    public PackageReference(String name) {
        this.name = name;
    }

    public boolean startsWith(String str) {
        return name.startsWith(str);
    }

	public boolean isNotUnder(PackageReference reference) {
		return !this.name.startsWith(reference.name + ".");
	}

    
    public PackageReference child(String relativeName) {
        return new PackageReference(this.name + "." + relativeName);
    }
    
    public boolean pointsInside(PackageReference reference) {
        return reference.startsWith(this.name + ".");
    }
    
    public String relativeNameTo(PackageReference reference) {
        return this.name.replaceFirst(reference.name + ".", "");
    }
    

    public boolean isDirectChildOfMe(PackageReference reference) {
        return !reference.relativeNameTo(this).contains(".");
    }

    public String firstPartOfRelativeNameTo(PackageReference reference) {
        return this.relativeNameTo(reference).split("\\.", 2)[0];
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PackageReference)) {
            return false;
        }
        PackageReference castOther = (PackageReference) other;

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

    public int compareTo(PackageReference that) {
        return name.compareTo(that.name);
    }

}
