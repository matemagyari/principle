package org.principle.domain.detector.cycledetector;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.principle.domain.core.DesingCheckerParameters;
import org.principle.domain.core.Package;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class PackageStructureBuilder {

	public Package build(Collection<Package> packages, DesingCheckerParameters parameters) {
		List<Package> sortedPackages = sortByName(packages, parameters.getBasePackage());
		Package basePackage = sortedPackages.remove(0);
		for (Package aPackage : sortedPackages) {
			basePackage.insert(aPackage);
		}
		return basePackage;
	}


	private List<Package> sortByName(Collection<Package> packages, final String basePackageName) {
		Comparator<Package> comparator = new Comparator<Package>() {

			public int compare(Package p1, Package p2) {
				return p1.getReference().getName().compareTo(p2.getReference().getName());
			}
		};
		// act
		List<Package> sortedPackages = Lists.newArrayList(packages);
		Collections.sort(sortedPackages, comparator);

		Predicate<Package> filter = new Predicate<Package>() {

			public boolean apply(Package input) {
				return input.getReference().startsWith(basePackageName);
			}
		};
		return Lists.newArrayList(Iterables.filter(sortedPackages, filter));
	}
}
