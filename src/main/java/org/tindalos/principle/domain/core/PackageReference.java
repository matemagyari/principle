package org.tindalos.principle.domain.core;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a reference to a Java package by its fully qualified name.
 * Provides hierarchical package navigation and comparison operations.
 *
 * @param name the fully qualified package name
 */
public record PackageReference(String name) implements Comparable<PackageReference> {

    public boolean startsWith(String str) {
        return name.startsWith(str);
    }

    public String createChild(String relativeNameOfDirectSubPackage) {
        return name + "." + relativeNameOfDirectSubPackage;
    }

    public boolean isDirectParentOf(PackageReference reference) {
        if (this.equals(reference)) {
            return false;
        }
        return !reference.relativeNameTo(this).contains(".");
    }

    public boolean isNotAnAncestorOf(PackageReference reference) {
        return !reference.pointsInside(this);
    }

    public PackageReference child(String relativeName) {
        return new PackageReference(name + "." + relativeName);
    }

    public boolean pointsInside(PackageReference reference) {
        return startsWith(reference.name + ".");
    }

    public boolean pointsToThatOrInside(PackageReference reference) {
        return this.equals(reference) || pointsInside(reference);
    }

    public boolean oneContainsAnother(PackageReference that) {
        return pointsToThatOrInside(that) || that.pointsToThatOrInside(this);
    }

    public boolean isDescendantOf(PackageReference reference) {
        return startsWith(reference.name + ".");
    }

    public String relativeNameTo(PackageReference reference) {
        if (!this.name.startsWith(reference.name + ".")) {
            throw new IllegalArgumentException(this + " is not under " + reference);
        }
        return name.replaceFirst(reference.name + "\\.", "");
    }

    public String firstPartOfRelativeNameTo(PackageReference reference) {
        return relativeNameTo(reference).split("\\.", 2)[0];
    }


    @Override
    public int compareTo(PackageReference that) {
        return name.compareTo(that.name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).hashCode();
    }
}

