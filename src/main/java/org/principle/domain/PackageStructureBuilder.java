package org.principle.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jdepend.framework.JavaPackage;

import org.principle.domain.core.Package;
import org.principle.domain.jdepend.LazyLoadingJDependBasedPackage;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class PackageStructureBuilder {

	private final String basePackage;

	public PackageStructureBuilder(String basePackage) {
		this.basePackage = basePackage;
	}

	public Package build(Collection<JavaPackage> packages) {
		List<JavaPackage> sortedPackages = sortByName(packages);

		JavaPackage baseJavaPackage = sortedPackages.remove(0);

		Package basePackage = transform(baseJavaPackage);

		for (JavaPackage javaPackage : sortedPackages) {
			basePackage.insert(transform(javaPackage));
		}

		return basePackage;
	}

	private Package transform(JavaPackage baseJavaPackage) {
		return new LazyLoadingJDependBasedPackage(baseJavaPackage, basePackage);
	}

	private List<JavaPackage> sortByName(Collection<JavaPackage> packages) {
		Comparator<JavaPackage> comparator = new Comparator<JavaPackage>() {

			public int compare(JavaPackage p1, JavaPackage p2) {
				return p1.getName().compareTo(p2.getName());
			}
		};
		// act
		List<JavaPackage> sortedPackages = Lists.newArrayList(packages);
		Collections.sort(sortedPackages, comparator);

		Predicate<JavaPackage> filter = new Predicate<JavaPackage>() {

			public boolean apply(JavaPackage input) {
				return input.getName().startsWith(basePackage);
			}
		};
		return Lists.newArrayList(Iterables.filter(sortedPackages, filter));
	}
}
