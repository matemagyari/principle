package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SubmoduleFactory {
    
    public List<Submodule> buildModules(SubmoduleDefinitions submoduleDefinitions, Map<PackageReference, Package> packageMap) {
        List<Submodule> submodules = Lists.newArrayList();
        for (SubmoduleDefinition submoduleDefinition : submoduleDefinitions) {
            Submodule aSubmodule = convert(packageMap, submoduleDefinition);
			submodules.add(aSubmodule);
        }
		return submodules;
	}

	private static Submodule convert(Map<PackageReference, Package> packageMap, SubmoduleDefinition submoduleDefinition) {
		
		Set<Package> packages = resolve(packageMap, submoduleDefinition.getPackages());
		Submodule submodule = new Submodule(submoduleDefinition.getId(), packages);
		submodule.setPlannedDependencies(submoduleDefinition.getPlannedDependencies());
		return submodule;
	}

    private static Set<Package> resolve(final Map<PackageReference, Package> packageMap, Set<PackageReference> packageReferences) {
        Function<PackageReference, Package> function = new Function<PackageReference, Package>() {
            
            public Package apply(PackageReference input) {
                Package thePackage = packageMap.get(input);
                if (thePackage == null) {
                	throw new InvalidBlueprintDefinitionException("Package does not exist: " + input);
                }
				return thePackage;
            }
        };
        return Sets.newHashSet(Iterables.transform(packageReferences, function));
    }

}
