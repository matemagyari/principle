package org.tindalos.principle.domain.core;

import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class Cycle implements Comparable<Cycle> {
    
    private final List<PackageReference> references;
    
    public Cycle(List<PackageReference> references) {
        if (references == null || references.size() < 2) {
            throw new DomainException("Invalid cycle " + references);
        }
        this.references = Lists.newArrayList(references);
    }
    public Cycle(PackageReference... references) {
        this(Lists.newArrayList(references));
    }
    
    public List<PackageReference> getReferences() {
        return references;
    }
    
    public PackageReference getLast() {
        return references.get(references.size()-1);
    }
    
    @Override
    public String toString() {
    	String arrow = "-->";
    	StringBuffer sb = new StringBuffer("*"+arrow);
    	for (PackageReference reference : references) {
			sb.append(reference + arrow);
		}
        return sb.append("*"). toString();
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Cycle)) {
            return false;
        }
        Cycle castOther = (Cycle) other;
        
        if (!Sets.newHashSet(references).equals(Sets.newHashSet(castOther.references))) {
        	return false;
        }
        
        int offset = castOther.references.indexOf(references.get(0));
        for (int i = 0; i < references.size(); i++) {
			int indexWithOffset = (i + offset) % references.size();
			if (!references.get(i).equals(castOther.references.get(indexWithOffset))) {
				return false;
			}
		}
        
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                        .append(references.size())
                        .hashCode();
    }
    public int compareTo(Cycle that) {
        return this.toString().compareTo(that.toString());
    }


}
