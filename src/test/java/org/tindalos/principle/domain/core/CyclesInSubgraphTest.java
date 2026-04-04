package org.tindalos.principle.domain.core;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.domain.core.packages.PackageMetrics;
import org.tindalos.principle.domain.core.packages.PackageReference;

/**
 * Unit tests for CyclesInSubgraph cycle tracking behavior.
 */
public class CyclesInSubgraphTest {

    private CyclesInSubgraph cyclesInSubgraph;
    private Package packageA;
    private Package packageB;
    private Package packageC;

    @Before
    public void setUp() {
        cyclesInSubgraph = CyclesInSubgraph.empty();
        packageA = createTestPackage("org.example.a");
        packageB = createTestPackage("org.example.b");
        packageC = createTestPackage("org.example.c");
    }

    @Test
    public void emptyCyclesInSubgraph_hasNoCyclesOrInvestigatedPackages() {
        CyclesInSubgraph empty = CyclesInSubgraph.empty();

        assertTrue(empty.cycles().isEmpty());
        assertTrue(empty.investigatedPackages().isEmpty());
    }

    @Test
    public void addCycle_storesCycleByBreakingPoint() {
        PackageReference refA = new PackageReference("org.example.a");
        PackageReference refB = new PackageReference("org.example.b");
        Cycle cycle = new Cycle(List.of(refA, refB));

        cyclesInSubgraph.add(cycle);

        var cycles = cyclesInSubgraph.cycles();
        assertEquals(1, cycles.size());
        assertTrue(cycles.containsKey(refB));
        assertEquals(1, cycles.get(refB).size());
    }

    @Test
    public void addMultipleCyclesWithSameBreakingPoint_keepsBothUnderSameEntry() {
        PackageReference refA = new PackageReference("org.example.a");
        PackageReference refB = new PackageReference("org.example.b");
        PackageReference refC = new PackageReference("org.example.c");

        Cycle cycle1 = new Cycle(List.of(refA, refB));
        Cycle cycle2 = new Cycle(List.of(refC, refB));

        cyclesInSubgraph.add(cycle1);
        cyclesInSubgraph.add(cycle2);

        var cycles = cyclesInSubgraph.cycles();
        assertEquals(1, cycles.size());
        assertTrue(cycles.containsKey(refB));
        assertEquals(2, cycles.get(refB).size());
    }

    @Test
    public void addMultipleCyclesWithDifferentBreakingPoints_createsSeparateEntries() {
        PackageReference refA = new PackageReference("org.example.a");
        PackageReference refB = new PackageReference("org.example.b");
        PackageReference refC = new PackageReference("org.example.c");

        Cycle cycle1 = new Cycle(List.of(refA, refB));
        Cycle cycle2 = new Cycle(List.of(refB, refC));

        cyclesInSubgraph.add(cycle1);
        cyclesInSubgraph.add(cycle2);

        var cycles = cyclesInSubgraph.cycles();
        assertEquals(2, cycles.size());
        assertTrue(cycles.containsKey(refB));
        assertTrue(cycles.containsKey(refC));
    }

    @Test
    public void addDuplicateCycle_doesNotStoreItTwice() {
        PackageReference refA = new PackageReference("org.example.a");
        PackageReference refB = new PackageReference("org.example.b");
        Cycle cycle = new Cycle(List.of(refA, refB));

        cyclesInSubgraph.add(cycle);
        cyclesInSubgraph.add(cycle);

        var cycles = cyclesInSubgraph.cycles();
        assertEquals(1, cycles.size());
        assertEquals(1, cycles.get(refB).size());
    }

    @Test
    public void rememberPackageAsInvestigated_addsPackage() {
        cyclesInSubgraph.rememberPackageAsInvestigated(packageA);

        var packages = cyclesInSubgraph.investigatedPackages();
        assertEquals(1, packages.size());
        assertTrue(packages.contains(packageA));
    }

    @Test
    public void rememberMultiplePackages_tracksAllDistinctPackages() {
        cyclesInSubgraph.rememberPackageAsInvestigated(packageA);
        cyclesInSubgraph.rememberPackageAsInvestigated(packageB);
        cyclesInSubgraph.rememberPackageAsInvestigated(packageC);

        var packages = cyclesInSubgraph.investigatedPackages();
        assertEquals(3, packages.size());
        assertTrue(packages.contains(packageA));
        assertTrue(packages.contains(packageB));
        assertTrue(packages.contains(packageC));
    }

    @Test
    public void isBreakingPoint_whenBelowLimit_returnsFalse() {
        PackageReference refB = new PackageReference("org.example.b");
        Cycle cycle = new Cycle(List.of(new PackageReference("org.example.a"), refB));

        cyclesInSubgraph.add(cycle);

        assertFalse(cyclesInSubgraph.isBreakingPoint(packageB));
    }

    @Test
    public void isBreakingPoint_whenAboveLimit_returnsTrue() {
        PackageReference refB = new PackageReference("org.example.b");

        for (int index = 1; index <= 6; index++) {
            PackageReference refOther = new PackageReference("org.example.other" + index);
            cyclesInSubgraph.add(new Cycle(List.of(refOther, refB)));
        }

        assertTrue(cyclesInSubgraph.isBreakingPoint(packageB));
    }

    @Test
    public void mergeIn_mergesCyclesAndInvestigatedPackages() {
        PackageReference refA = new PackageReference("org.example.a");
        PackageReference refB = new PackageReference("org.example.b");
        PackageReference refC = new PackageReference("org.example.c");

        cyclesInSubgraph.add(new Cycle(List.of(refA, refB)));
        cyclesInSubgraph.rememberPackageAsInvestigated(packageA);

        CyclesInSubgraph other = CyclesInSubgraph.empty();
        other.add(new Cycle(List.of(refB, refC)));
        other.rememberPackageAsInvestigated(packageB);

        cyclesInSubgraph.mergeIn(other);

        assertEquals(2, cyclesInSubgraph.cycles().size());
        assertEquals(2, cyclesInSubgraph.investigatedPackages().size());
        assertTrue(cyclesInSubgraph.investigatedPackages().contains(packageA));
        assertTrue(cyclesInSubgraph.investigatedPackages().contains(packageB));
    }

    @Test
    public void toString_containsKeySections() {
        PackageReference refA = new PackageReference("org.example.a");
        PackageReference refB = new PackageReference("org.example.b");
        cyclesInSubgraph.add(new Cycle(List.of(refA, refB)));
        cyclesInSubgraph.rememberPackageAsInvestigated(packageA);

        String value = cyclesInSubgraph.toString();

        assertTrue(value.contains("CyclesInSubgraph"));
        assertTrue(value.contains("cycles="));
        assertTrue(value.contains("investigatedPackages="));
    }

    private static Package createTestPackage(String name) {
        return new Package(name) {
            @Override
            public boolean isUnreferred() {
                return false;
            }

            @Override
            public PackageMetrics getMetrics() {
                return PackageMetrics.UNDEFINED;
            }

            @Override
            public Set<PackageReference> getOwnPackageReferences() {
                return Collections.emptySet();
            }

            @Override
            public Set<PackageReference> getOwnExternalPackageReferences() {
                return Collections.emptySet();
            }
        };
    }
}
