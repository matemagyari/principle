package org.tindalos.principle.internal.domain.analyzers.submodulesblueprint;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.tindalos.principle.internal.domain.constraints.submodules.InvalidBlueprintDefinitionException;
import org.tindalos.principle.internal.domain.constraints.submodules.SubmoduleId;
import org.tindalos.principle.internal.domain.core.packages.PackageMetrics;
import org.tindalos.principle.internal.domain.core.packages.PackageReference;
import org.tindalos.principle.internal.domain.core.packages.PackageWithMetrics;

/**
 * Unit tests for Submodule verifying dependency detection logic.
 */
public class SubmoduleTest {

    private Submodule submodule(String id, Set<PackageWithMetrics> packages, Set<SubmoduleId> plannedDeps) {
        return new Submodule(new SubmoduleId(id), packages, plannedDeps);
    }

    private Submodule emptySubmodule(String id, String... plannedDeps) {
        Set<SubmoduleId> deps = new java.util.HashSet<>();
        for (String dep : plannedDeps) deps.add(new SubmoduleId(dep));
        return submodule(id, Set.of(), deps);
    }

    private PackageWithMetrics pkg(String name, String... efferentRefs) {
        Set<PackageReference> refs = new java.util.HashSet<>();
        for (String ref : efferentRefs) refs.add(new PackageReference(ref));
        return new PackageWithMetrics() {
            public PackageReference reference() { return new PackageReference(name); }
            public PackageMetrics getMetrics() { return new PackageMetrics(0, 0, 0, 0, 0); }
            public java.util.Set<PackageReference> getOwnPackageReferences() { return Set.of(); }
            public java.util.Set<PackageReference> getOwnExternalPackageReferences() { return Set.of(); }
            public java.util.Set<PackageReference> accumulatedDirectPackageReferences() { return Set.copyOf(refs); }
        };
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    @Test(expected = InvalidBlueprintDefinitionException.class)
    public void constructor_selfDependency_throwsException() {
        new Submodule(new SubmoduleId("MOD1"), Set.of(), Set.of(new SubmoduleId("MOD1")));
    }

    @Test
    public void constructor_packagesAndDependencies_areStoredAsImmutableCopies() {
        var mod = emptySubmodule("MOD1", "MOD2");
        assertEquals(Set.of(new SubmoduleId("MOD2")), mod.plannedDependencies);
        assertEquals(Set.of(), mod.packagesUnderModule);
    }

    // -------------------------------------------------------------------------
    // findIllegalDependencies
    // -------------------------------------------------------------------------

    @Test
    public void findIllegalDependencies_noReferences_returnsEmpty() {
        var mod1 = emptySubmodule("MOD1");
        var mod2 = emptySubmodule("MOD2");

        var illegal = mod1.findIllegalDependencies(Set.of(mod2));

        assertTrue(illegal.isEmpty());
    }

    @Test
    public void findIllegalDependencies_referenceToPlannedDependency_returnsEmpty() {
        var mod1 = submodule("MOD1",
                Set.of(pkg("com.example.mod1", "com.example.mod2")),
                Set.of(new SubmoduleId("MOD2")));
        var mod2 = submodule("MOD2",
                Set.of(pkg("com.example.mod2")),
                Set.of());

        var illegal = mod1.findIllegalDependencies(Set.of(mod2));

        assertTrue(illegal.isEmpty());
    }

    @Test
    public void findIllegalDependencies_referenceToUnplannedModule_returnsViolation() {
        var mod1 = submodule("MOD1",
                Set.of(pkg("com.example.mod1", "com.example.mod2")),
                Set.of());
        var mod2 = submodule("MOD2",
                Set.of(pkg("com.example.mod2")),
                Set.of());

        var illegal = mod1.findIllegalDependencies(Set.of(mod2));

        assertEquals(Set.of(mod2), illegal);
    }

    @Test
    public void findIllegalDependencies_referenceToSubPackage_isDetected() {
        var mod1 = submodule("MOD1",
                Set.of(pkg("com.example.mod1", "com.example.mod2.sub")),
                Set.of());
        var mod2 = submodule("MOD2",
                Set.of(pkg("com.example.mod2")),
                Set.of());

        var illegal = mod1.findIllegalDependencies(Set.of(mod2));

        assertEquals(Set.of(mod2), illegal);
    }

    // -------------------------------------------------------------------------
    // findMissingPredefinedDependencies
    // -------------------------------------------------------------------------

    @Test
    public void findMissingPredefinedDependencies_noDeclaredDeps_returnsEmpty() {
        var mod1 = emptySubmodule("MOD1");
        var mod2 = emptySubmodule("MOD2");

        var missing = mod1.findMissingPredefinedDependencies(Set.of(mod2));

        assertTrue(missing.isEmpty());
    }

    @Test
    public void findMissingPredefinedDependencies_dependencyPresent_returnsEmpty() {
        var mod1 = submodule("MOD1",
                Set.of(pkg("com.example.mod1", "com.example.mod2")),
                Set.of(new SubmoduleId("MOD2")));
        var mod2 = submodule("MOD2",
                Set.of(pkg("com.example.mod2")),
                Set.of());

        var missing = mod1.findMissingPredefinedDependencies(Set.of(mod2));

        assertTrue(missing.isEmpty());
    }

    @Test
    public void findMissingPredefinedDependencies_declaredButNotReferenced_returnsViolation() {
        var mod1 = submodule("MOD1",
                Set.of(pkg("com.example.mod1")),   // no outgoing refs
                Set.of(new SubmoduleId("MOD2")));
        var mod2 = submodule("MOD2",
                Set.of(pkg("com.example.mod2")),
                Set.of());

        var missing = mod1.findMissingPredefinedDependencies(Set.of(mod2));

        assertEquals(Set.of(mod2), missing);
    }

    // -------------------------------------------------------------------------
    // equals / hashCode
    // -------------------------------------------------------------------------

    @Test
    public void equals_sameId_areEqual() {
        assertEquals(emptySubmodule("MOD1"), emptySubmodule("MOD1"));
    }

    @Test
    public void equals_differentId_areNotEqual() {
        assertNotEquals(emptySubmodule("MOD1"), emptySubmodule("MOD2"));
    }

    @Test
    public void hashCode_sameId_sameHashCode() {
        assertEquals(emptySubmodule("MOD1").hashCode(), emptySubmodule("MOD1").hashCode());
    }

    @Test
    public void toString_returnsId() {
        assertEquals("MOD1", emptySubmodule("MOD1").toString());
    }
}

