package org.tindalos.principle.domain.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class PackageSorter {

	public List<Package> sortByName(List<Package> packages, final String basePackageName) {
		
		List<Package> sortedPackages = sortByName(packages);

		Predicate<Package> filter = new Predicate<Package>() {

			public boolean apply(Package input) {
				return input.getReference().startsWith(basePackageName);
			}
		};
		return Lists.newArrayList(Iterables.filter(sortedPackages, filter));
	}

	public List<Package> sortByName(List<Package> packages) {
		
		Comparator<Package> comparator = new Comparator<Package>() {

			public int compare(Package p1, Package p2) {
				return p1.getReference().getName().compareTo(p2.getReference().getName());
			}
		};
		List<Package> sortedPackages = Lists.newArrayList(packages);
		Collections.sort(sortedPackages, comparator);

		return sortedPackages;
	}
}
