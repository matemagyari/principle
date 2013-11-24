package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.collect.Sets;

public class SubmoduleDefinition {

	private final SubmoduleId id;
	private final Set<PackageReference> packages;
	private final Set<SubmoduleId> legalDependencies;

	public SubmoduleDefinition(SubmoduleId id, Set<PackageReference> packages) {
		this.id = id;
		this.packages = Sets.newHashSet(packages);
		this.legalDependencies = Sets.newHashSet();
	}

	public SubmoduleId getId() {
		return id;
	}

	public Set<PackageReference> getPackages() {
		return Sets.newHashSet(packages);
	}

	public Set<SubmoduleId> getPlannedDependencies() {
		return Sets.newHashSet(legalDependencies);
	}

	public void addPlannedDependencies(Collection<SubmoduleId> plannedDependencies) {
		this.legalDependencies.addAll(plannedDependencies);
	}

	public boolean overlapsWith(SubmoduleDefinition that) {
		for (PackageReference aPackage : this.packages) {
			for (PackageReference otherPackage : that.packages) {
				if (aPackage.oneContainsAnother(otherPackage)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SubmoduleDefinition)) {
			return false;
		}
		SubmoduleDefinition castOther = (SubmoduleDefinition) other;

		return id.equals(castOther.id);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).hashCode();
	}

	@Override
	public String toString() {
		return "SubmoduleDefinition [" + id + "]";
	}

}
