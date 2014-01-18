package org.tindalos.principle.domain.core;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PackageReference extends PackageReferenceScala implements Comparable<PackageReference> {

    public PackageReference(String name) {
       super(name);
    }
    /*
    public boolean startsWith(String str) {
        return name().startsWith(str);
    }
    
	public String createChild(String relativeNameOfDirectSubPackage) {
		return name() + "." + relativeNameOfDirectSubPackage;
	}

    public boolean isDirectParentOf(PackageReference reference) {
    	if (this.equals(reference)) return false;
        return !reference.relativeNameTo(this).contains(".");
    }
    public boolean isNotAnAncestorOf(PackageReference reference) {
        return !isAnAncestorOf(reference);
    }
    private boolean isAnAncestorOf(PackageReference reference) {
    	return reference.pointsInside(this);
    }
    
    public PackageReference child(String relativeName) {
        return new PackageReference(this.name() + "." + relativeName);
    }
    
    public boolean pointsInside(PackageReference reference) {
        return this.startsWith(reference.name() + ".");
    }
     */

    public boolean pointsToThatOrInside(PackageReference reference) {
        return this.equals(reference) || this.pointsInside(reference);
    }

	public boolean oneContainsAnother(PackageReference that) {
		return this.pointsToThatOrInside(that) || that.pointsToThatOrInside(this);
	}
    
    public boolean isDescendantOf(PackageReference reference) {
        return this.startsWith(reference.name() + ".");
    }
    
    public String relativeNameTo(PackageReference reference) {
    	if (!this.name().startsWith(reference.name() + ".")) {
    		throw new IllegalArgumentException(this + " is not under " + reference);
    	}
        return this.name().replaceFirst(reference.name() + ".", "");
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

        return name().equals(castOther.name());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name()).hashCode();
    }

    @Override
    public String toString() {
        return name();
    }

    public int compareTo(PackageReference that) {
        return name().compareTo(that.name());
    }

}
