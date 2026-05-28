package org.tindalos.guardrails.internal.domain.core.packages;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuildingException;
import org.tindalos.guardrails.internal.infrastructure.packages.MutablePackage;

public class PackageTest {

    @Test
    public void testPackageCreation() {
        Package pkg = new Package(new PackageReference("org.example.test"), PackageMetrics.UNDEFINED, Set.of(), Set.of(), false, List.of());
        assertEquals("org.example.test", pkg.reference().name());
        assertFalse(pkg.isUnreferred());
        assertEquals(0, pkg.subPackages().size());
    }

    @Test
    public void testPackageCreationWithString() {
        Package pkg = new Package("org.example.test");
        assertEquals("org.example.test", pkg.reference().name());
    }

    @Test
    public void testInsertDirectSubPackage() {
        MutablePackage parent = new MutablePackage(new PackageReference("org.example"));
        MutablePackage child = new MutablePackage(new PackageReference("org.example.child"));

        parent.insert(child);

        assertEquals(1, parent.subPackages().size());
        assertTrue(parent.subPackages().contains(child));

        Package immutableParent = parent.toImmutable();
        assertEquals(1, immutableParent.subPackages().size());
        assertEquals("org.example.child", immutableParent.subPackages().get(0).reference().name());
    }

    @Test
    public void testInsertIndirectSubPackage() {
        MutablePackage parent = new MutablePackage(new PackageReference("org.example"));
        MutablePackage grandchild = new MutablePackage(new PackageReference("org.example.child.grandchild"));

        parent.insert(grandchild);

        assertEquals(1, parent.subPackages().size());
        MutablePackage child = parent.subPackages().get(0);
        assertEquals("org.example.child", child.reference().name());
        assertEquals(1, child.subPackages().size());
        assertEquals("org.example.child.grandchild", child.subPackages().get(0).reference().name());

        Package immutableParent = parent.toImmutable();
        assertEquals(1, immutableParent.subPackages().size());
        Package immutableChild = immutableParent.subPackages().get(0);
        assertEquals("org.example.child", immutableChild.reference().name());
        assertEquals(1, immutableChild.subPackages().size());
        assertEquals("org.example.child.grandchild", immutableChild.subPackages().get(0).reference().name());
    }

    @Test
    public void testInsertIntoItself_shouldThrowException() {
        MutablePackage pkg = new MutablePackage(new PackageReference("org.example"));
        assertThrows(PackageStructureBuildingException.class, () -> pkg.insert(pkg));
    }

    @Test
    public void testInsertUnrelatedPackage_shouldThrowException() {
        MutablePackage parent = new MutablePackage(new PackageReference("org.example"));
        MutablePackage unrelated = new MutablePackage(new PackageReference("org.other"));

        assertThrows(PackageStructureBuildingException.class, () -> parent.insert(unrelated));
    }

    @Test
    public void testToMap() {
        MutablePackage parent = new MutablePackage(new PackageReference("org.example"));
        MutablePackage child1 = new MutablePackage(new PackageReference("org.example.child1"));
        MutablePackage child2 = new MutablePackage(new PackageReference("org.example.child2"));

        parent.insert(child1);
        parent.insert(child2);

        Package immutableParent = parent.toImmutable();
        Map<PackageReference, Package> map = immutableParent.toMap();

        assertEquals(3, map.size());
        assertTrue(map.containsKey(immutableParent.reference()));
        assertTrue(map.containsKey(child1.reference()));
        assertTrue(map.containsKey(child2.reference()));
    }

    @Test
    public void testAccumulatedDirectPackageReferences() {
        PackageReference ref1 = new PackageReference("org.external.lib1");
        PackageReference ref2 = new PackageReference("org.external.lib2");
        Package pkg = new Package(new PackageReference("org.example.test"), PackageMetrics.UNDEFINED, Set.of(ref1, ref2), Set.of(), false, List.of());

        Set<PackageReference> refs = pkg.accumulatedDirectPackageReferences();

        assertEquals(2, refs.size());
        assertTrue(refs.contains(ref1));
        assertTrue(refs.contains(ref2));
    }

    @Test
    public void testAccumulatedDirectPackageReferences_withSubPackages() {
        PackageReference refFromChild = new PackageReference("org.external.lib");
        MutablePackage parent = new MutablePackage(new PackageReference("org.example"));
        MutablePackage child = new MutablePackage(
            new PackageReference("org.example.child"),
            PackageMetrics.UNDEFINED,
            Set.of(refFromChild),
            Set.of(),
            false
        );

        parent.insert(child);

        Package immutableParent = parent.toImmutable();
        Set<PackageReference> refs = immutableParent.accumulatedDirectPackageReferences();

        assertTrue(refs.contains(refFromChild));
    }

    @Test
    public void testInstability() {
        Package pkg = new Package(new PackageReference("org.example"), new PackageMetrics(2, 3, 0.5f, 0.6f, 0.1f), Set.of(), Set.of(), false, List.of());

        assertEquals(0.6f, pkg.metrics().instability(), 0.001);
    }

    @Test
    public void testDistance() {
        Package pkg = new Package(new PackageReference("org.example"), new PackageMetrics(2, 3, 0.5f, 0.6f, 0.1f), Set.of(), Set.of(), false, List.of());

        assertEquals(0.1f, pkg.metrics().distance(), 0.001);
    }

    @Test
    public void testIsIsolated_whenNoConnections() {
        Package pkg = new Package(new PackageReference("org.example"), new PackageMetrics(0, 0, 0, 0, 0), Set.of(), Set.of(), false, List.of());

        assertTrue(pkg.metrics().isIsolated());
    }

    @Test
    public void testIsIsolated_whenHasAfferentCoupling() {
        Package pkg = new Package(new PackageReference("org.example"), new PackageMetrics(1, 0, 0, 0, 0), Set.of(), Set.of(), false, List.of());

        assertFalse(pkg.metrics().isIsolated());
    }

    @Test
    public void testIsIsolated_whenHasEfferentCoupling() {
        Package pkg = new Package(new PackageReference("org.example"), new PackageMetrics(0, 1, 0, 0, 0), Set.of(), Set.of(), false, List.of());

        assertFalse(pkg.metrics().isIsolated());
    }

    @Test
    public void testEquals_sameReference() {
        Package pkg1 = new Package(new PackageReference("org.example"));
        Package pkg2 = new Package(new PackageReference("org.example"));

        assertEquals(pkg1, pkg2);
    }

    @Test
    public void testEquals_differentReference() {
        Package pkg1 = new Package(new PackageReference("org.example.a"));
        Package pkg2 = new Package(new PackageReference("org.example.b"));

        assertNotEquals(pkg1, pkg2);
    }

    @Test
    public void testHashCode_sameReference() {
        Package pkg1 = new Package(new PackageReference("org.example"));
        Package pkg2 = new Package(new PackageReference("org.example"));

        assertEquals(pkg1.hashCode(), pkg2.hashCode());
    }

    @Test
    public void testToString() {
        Package pkg = new Package(new PackageReference("org.example.test"));

        assertEquals("org.example.test", pkg.toString());
    }

    @Test
    public void testMultipleLevelInsertion() {
        MutablePackage root = new MutablePackage(new PackageReference("org"));
        MutablePackage level3 = new MutablePackage(new PackageReference("org.example.app.service"));

        root.insert(level3);

        Package immutableRoot = root.toImmutable();

        assertEquals(1, immutableRoot.subPackages().size());
        Package org_example = immutableRoot.subPackages().get(0);
        assertEquals("org.example", org_example.reference().name());

        assertEquals(1, org_example.subPackages().size());
        Package org_example_app = org_example.subPackages().get(0);
        assertEquals("org.example.app", org_example_app.reference().name());

        assertEquals(1, org_example_app.subPackages().size());
        assertEquals("org.example.app.service", org_example_app.subPackages().get(0).reference().name());
    }
}

