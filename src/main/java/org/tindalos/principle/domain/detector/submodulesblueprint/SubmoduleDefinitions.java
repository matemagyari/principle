package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SubmoduleDefinitions implements Iterable<SubmoduleDefinition> {

	private final Map<SubmoduleId, SubmoduleDefinition> definitions;

	public SubmoduleDefinitions(Map<SubmoduleId, SubmoduleDefinition> definitions) {

		checkNoOverlaps(definitions.values());
		this.definitions = Maps.newHashMap(definitions);
	}

	private static void checkNoOverlaps(Collection<SubmoduleDefinition> definitions) {
		Set<Overlap> overlaps = Sets.newHashSet();
		for (SubmoduleDefinition submoduleDefinition : definitions) {
			Set<SubmoduleDefinition> otherDefinitions = allBut(definitions, submoduleDefinition);
			for (SubmoduleDefinition anOtherDefinition : otherDefinitions) {
				if (submoduleDefinition.overlapsWith(anOtherDefinition)) {
					overlaps.add(new Overlap(submoduleDefinition.getId(), anOtherDefinition.getId()));
				}
			}
		}
		if (!overlaps.isEmpty()) {
			throw new OverlappingSubmoduleDefinitionsException(overlaps);
		}
	}

	public SubmoduleDefinitions(List<SubmoduleDefinition> theDefinitions) {
		this.definitions = Maps.newHashMap();
		for (SubmoduleDefinition submoduleDefinition : theDefinitions) {
			definitions.put(submoduleDefinition.getId(), submoduleDefinition);
		}
	}

	public Iterator<SubmoduleDefinition> iterator() {
		return definitions.values().iterator();
	}

	public Set<PackageReference> getPackages(SubmoduleId submoduleId) {
		return definitions.get(submoduleId).getPackages();
	}

	private static Set<SubmoduleDefinition> allBut(Collection<SubmoduleDefinition> definitions, SubmoduleDefinition definition) {
		Set<SubmoduleDefinition> definitionsCopy = Sets.newHashSet(definitions);
		definitionsCopy.remove(definition);
		return definitionsCopy;
	}

	public static class Overlap {
		private final Set<SubmoduleId> submoduleIds;

		public Overlap(SubmoduleId submodule1, SubmoduleId submodule2) {
			this.submoduleIds = Sets.newHashSet(submodule1, submodule2);
		}

		public Set<SubmoduleId> submoduleIds() {
			return Sets.newHashSet(submoduleIds);
		}
		
	    @Override
	    public boolean equals(Object other) {
	        if (!(other instanceof Overlap)) {
	            return false;
	        }
	        Overlap castOther = (Overlap) other;

	        return new EqualsBuilder()
	        	.append(submoduleIds,castOther.submoduleIds)
	        	.isEquals();
	    }

	    @Override
	    public int hashCode() {
	        return new HashCodeBuilder()
        		.append(submoduleIds)
	        	.hashCode();
	    }

	}
}
