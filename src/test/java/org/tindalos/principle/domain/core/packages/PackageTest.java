package org.tindalos.principle.domain.core.packages;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageStructureBuildingException;
import scala.collection.JavaConverters;

public class PackageTest {

    private TestPackage packageA;
    private TestPackage packageB;
    private TestPackage packageC;

    @Before
    public void setUp() {
        packageA = new TestPackage("org.example.a");
        packageB = new TestPackage("org.example.b");
        packageC = new TestPackage("org.example.c");
    }

    @Test
    public void testPackageCreation() {
        TestPackage pkg = new TestPackage("org.example.test");
        Assert.assertEquals("org.example.test", pkg.reference().name());
        assertFalse(pkg.isUnreferred());
        Assert.assertEquals(0, pkg.subPackages().size());
    }

    @Test
    public void testPackageCreationWithString() {
        TestPackage pkg = new TestPackage("org.example.test");
        Assert.assertEquals("org.example.test", pkg.reference().name());
    }

    @Test
    public void testInsertDirectSubPackage() {
        TestPackage parent = new TestPackage("org.example");
        TestPackage child = new TestPackage("org.example.child");

        parent.insert(child);

        Assert.assertEquals(1, parent.subPackages().size());
        assertTrue(parent.subPackages().contains(child));
    }

    @Test
    public void testInsertIndirectSubPackage() {
        TestPackage parent = new TestPackage("org.example");
        TestPackage grandchild = new TestPackage("org.example.child.grandchild");

        parent.insert(grandchild);

        Assert.assertEquals(1, parent.subPackages().size());
        org.tindalos.principle.domain.core.Package child = parent.subPackages().head();
        assertEquals("org.example.child", child.reference().name());
        assertEquals(1, child.subPackages().size());
        assertEquals("org.example.child.grandchild", child.subPackages().head().reference().name());
    }

    @Test(expected = PackageStructureBuildingException.class)
    public void testInsertIntoItself_shouldThrowException() {
        TestPackage pkg = new TestPackage("org.example");
        pkg.insert(pkg);
    }

    @Test(expected = PackageStructureBuildingException.class)
    public void testInsertUnrelatedPackage_shouldThrowException() {
        TestPackage parent = new TestPackage("org.example");
        TestPackage unrelated = new TestPackage("org.other");

        parent.insert(unrelated);
    }

    @Test
    public void testToMap() {
        TestPackage parent = new TestPackage("org.example");
        TestPackage child1 = new TestPackage("org.example.child1");
        TestPackage child2 = new TestPackage("org.example.child2");

        parent.insert(child1);
        parent.insert(child2);

        Map<PackageReference, org.tindalos.principle.domain.core.Package> map = JavaConverters.mapAsJavaMap(parent.toMap());

        assertEquals(3, map.size());
        assertTrue(map.containsKey(parent.reference()));
        assertTrue(map.containsKey(child1.reference()));
        assertTrue(map.containsKey(child2.reference()));
    }

    @Test
    public void testAccumulatedDirectPackageReferences() {
        TestPackage pkg = new TestPackage("org.example.test");
        PackageReference ref1 = new PackageReference("org.external.lib1");
        PackageReference ref2 = new PackageReference("org.external.lib2");
        pkg.addOwnReference(ref1);
        pkg.addOwnReference(ref2);

        Set<PackageReference> refs = JavaConverters.setAsJavaSet(pkg.accumulatedDirectPackageReferences());

        assertEquals(2, refs.size());
        assertTrue(refs.contains(ref1));
        assertTrue(refs.contains(ref2));
    }

    @Test
    public void testAccumulatedDirectPackageReferences_withSubPackages() {
        TestPackage parent = new TestPackage("org.example");
        TestPackage child = new TestPackage("org.example.child");
        PackageReference refFromChild = new PackageReference("org.external.lib");
        child.addOwnReference(refFromChild);

        parent.insert(child);

        Set<PackageReference> refs = JavaConverters.setAsJavaSet(parent.accumulatedDirectPackageReferences());

        assertTrue(refs.contains(refFromChild));
    }

    @Test
    public void testInstability() {
        TestPackage pkg = new TestPackage("org.example");
        pkg.setMetrics(new PackageMetrics(2, 3, 0.5f, 0.6f, 0.1f));

        Assert.assertEquals(0.6f, pkg.instability(), 0.001);
    }

    @Test
    public void testDistance() {
        TestPackage pkg = new TestPackage("org.example");
        pkg.setMetrics(new PackageMetrics(2, 3, 0.5f, 0.6f, 0.1f));

        Assert.assertEquals(0.1f, pkg.distance(), 0.001);
    }

    @Test
    public void testIsIsolated_whenNoConnections() {
        TestPackage pkg = new TestPackage("org.example");
        pkg.setMetrics(new PackageMetrics(0, 0, 0, 0, 0));

        assertTrue(pkg.isIsolated());
    }

    @Test
    public void testIsIsolated_whenHasAfferentCoupling() {
        TestPackage pkg = new TestPackage("org.example");
        pkg.setMetrics(new PackageMetrics(1, 0, 0, 0, 0));

        assertFalse(pkg.isIsolated());
    }

    @Test
    public void testIsIsolated_whenHasEfferentCoupling() {
        TestPackage pkg = new TestPackage("org.example");
        pkg.setMetrics(new PackageMetrics(0, 1, 0, 0, 0));

        assertFalse(pkg.isIsolated());
    }

    @Test
    public void testEquals_sameReference() {
        TestPackage pkg1 = new TestPackage("org.example");
        TestPackage pkg2 = new TestPackage("org.example");

        assertEquals(pkg1, pkg2);
    }

    @Test
    public void testEquals_differentReference() {
        TestPackage pkg1 = new TestPackage("org.example.a");
        TestPackage pkg2 = new TestPackage("org.example.b");

        assertNotEquals(pkg1, pkg2);
    }

    @Test
    public void testHashCode_sameReference() {
        TestPackage pkg1 = new TestPackage("org.example");
        TestPackage pkg2 = new TestPackage("org.example");

        assertEquals(pkg1.hashCode(), pkg2.hashCode());
    }

    @Test
    public void testToString() {
        TestPackage pkg = new TestPackage("org.example.test");

        assertEquals("org.example.test", pkg.toString());
    }

    @Test
    public void testMultipleLevelInsertion() {
        TestPackage root = new TestPackage("org");
        TestPackage level1 = new TestPackage("org.example");
        TestPackage level2 = new TestPackage("org.example.app");
        TestPackage level3 = new TestPackage("org.example.app.service");

        root.insert(level3);

        Assert.assertEquals(1, root.subPackages().size());
        org.tindalos.principle.domain.core.Package org_example = root.subPackages().head();
        assertEquals("org.example", org_example.reference().name());

        assertEquals(1, org_example.subPackages().size());
        org.tindalos.principle.domain.core.Package org_example_app = org_example.subPackages().head();
        assertEquals("org.example.app", org_example_app.reference().name());

        assertEquals(1, org_example_app.subPackages().size());
        assertEquals("org.example.app.service", org_example_app.subPackages().head().reference().name());
    }

    /**
     * Simple test implementation of Package for testing purposes.
     */
    private static class TestPackage extends Package {
        private final Set<PackageReference> ownReferences = new HashSet<>();
        private final Set<PackageReference> externalReferences = new HashSet<>();
        private PackageMetrics metrics = PackageMetrics.UNDEFINED;
        private boolean unreferred = false;

        public TestPackage(String referenceName) {
            super(referenceName);
        }

        public TestPackage(PackageReference reference) {
            super(reference);
        }

        public void addOwnReference(PackageReference ref) {
            ownReferences.add(ref);
        }

        public void addExternalReference(PackageReference ref) {
            externalReferences.add(ref);
        }

        public void setMetrics(PackageMetrics metrics) {
            this.metrics = metrics;
        }

        public void setUnreferred(boolean unreferred) {
            this.unreferred = unreferred;
        }

        @Override
        public boolean isUnreferred() {
            return unreferred;
        }

        @Override
        public PackageMetrics getMetrics() {
            return metrics;
        }

        @Override
        public scala.collection.immutable.Set<PackageReference> getOwnPackageReferences() {
            return JavaConverters.asScalaSet(ownReferences).toSet();
        }

        @Override
        public scala.collection.immutable.Set<PackageReference> getOwnExternalPackageReferences() {
            return JavaConverters.asScalaSet(externalReferences).toSet();
        }
    }
}

