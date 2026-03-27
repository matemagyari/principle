package org.tindalos.principle.domain.core

import org.junit.{Before, Test}
import org.junit.Assert._
import org.tindalos.principle.domain.core.packages.{PackageMetrics, PackageReference}

class CyclesInSubgraphTest {

  var cyclesInSubgraph: CyclesInSubgraph = _
  var packageA: Package = _
  var packageB: Package = _
  var packageC: Package = _

  @Before
  def setUp(): Unit = {
    cyclesInSubgraph = CyclesInSubgraph.empty()
    packageA = createTestPackage("org.example.a")
    packageB = createTestPackage("org.example.b")
    packageC = createTestPackage("org.example.c")
  }

  @Test
  def testEmptyCyclesInSubgraph(): Unit = {
    val empty = CyclesInSubgraph.empty()

    assertTrue(empty.cycles.isEmpty)
    assertTrue(empty.investigatedPackages.isEmpty)
  }

  @Test
  def testAddCycle(): Unit = {
    val refA = new PackageReference("org.example.a")
    val refB = new PackageReference("org.example.b")
    val cycle = new Cycle(java.util.Arrays.asList(refA, refB))

    cyclesInSubgraph.add(cycle)

    val cycles = cyclesInSubgraph.cycles
    assertEquals(1, cycles.size)
    assertTrue(cycles.containsKey(refB))
    assertEquals(1, cycles.get(refB).size)
  }

  @Test
  def testAddMultipleCyclesWithSameBreakingPoint(): Unit = {
    val refA = new PackageReference("org.example.a")
    val refB = new PackageReference("org.example.b")
    val refC = new PackageReference("org.example.c")

    val cycle1 = new Cycle(java.util.Arrays.asList(refA, refB))
    val cycle2 = new Cycle(java.util.Arrays.asList(refC, refB))

    cyclesInSubgraph.add(cycle1)
    cyclesInSubgraph.add(cycle2)

    val cycles = cyclesInSubgraph.cycles
    assertEquals(1, cycles.size)
    assertTrue(cycles.containsKey(refB))
    assertEquals(2, cycles.get(refB).size)
  }

  @Test
  def testAddMultipleCyclesWithDifferentBreakingPoints(): Unit = {
    val refA = new PackageReference("org.example.a")
    val refB = new PackageReference("org.example.b")
    val refC = new PackageReference("org.example.c")

    val cycle1 = new Cycle(java.util.Arrays.asList(refA, refB))
    val cycle2 = new Cycle(java.util.Arrays.asList(refB, refC))

    cyclesInSubgraph.add(cycle1)
    cyclesInSubgraph.add(cycle2)

    val cycles = cyclesInSubgraph.cycles
    assertEquals(2, cycles.size)
    assertTrue(cycles.containsKey(refB))
    assertTrue(cycles.containsKey(refC))
  }

  @Test
  def testAddDuplicateCycle(): Unit = {
    val refA = new PackageReference("org.example.a")
    val refB = new PackageReference("org.example.b")
    val cycle = new Cycle(java.util.Arrays.asList(refA, refB))

    cyclesInSubgraph.add(cycle)
    cyclesInSubgraph.add(cycle)

    val cycles = cyclesInSubgraph.cycles
    assertEquals(1, cycles.size)
    assertEquals(1, cycles.get(refB).size)
  }

  @Test
  def testRememberPackageAsInvestigated(): Unit = {
    cyclesInSubgraph.rememberPackageAsInvestigated(packageA)

    val packages = cyclesInSubgraph.investigatedPackages
    assertEquals(1, packages.size)
    assertTrue(packages.contains(packageA))
  }

  @Test
  def testRememberMultiplePackages(): Unit = {
    cyclesInSubgraph.rememberPackageAsInvestigated(packageA)
    cyclesInSubgraph.rememberPackageAsInvestigated(packageB)
    cyclesInSubgraph.rememberPackageAsInvestigated(packageC)

    val packages = cyclesInSubgraph.investigatedPackages
    assertEquals(3, packages.size)
    assertTrue(packages.contains(packageA))
    assertTrue(packages.contains(packageB))
    assertTrue(packages.contains(packageC))
  }

  @Test
  def testIsBreakingPoint_whenNotBreakingPoint(): Unit = {
    val refB = new PackageReference("org.example.b")
    val cycle = new Cycle(java.util.Arrays.asList(new PackageReference("org.example.a"), refB))

    cyclesInSubgraph.add(cycle)

    assertFalse(cyclesInSubgraph.isBreakingPoint(packageB))
  }

  @Test
  def testIsBreakingPoint_whenIsBreakingPoint(): Unit = {
    val refB = new PackageReference("org.example.b")

    // Add more than 5 cycles (LIMIT = 5)
    for (i <- 1 to 6) {
      val refOther = new PackageReference(s"org.example.other$i")
      val cycle = new Cycle(java.util.Arrays.asList(refOther, refB))
      cyclesInSubgraph.add(cycle)
    }

    assertTrue(cyclesInSubgraph.isBreakingPoint(packageB))
  }

  @Test
  def testMergeIn(): Unit = {
    val refA = new PackageReference("org.example.a")
    val refB = new PackageReference("org.example.b")
    val refC = new PackageReference("org.example.c")

    val cycle1 = new Cycle(java.util.Arrays.asList(refA, refB))
    cyclesInSubgraph.add(cycle1)
    cyclesInSubgraph.rememberPackageAsInvestigated(packageA)

    val other = CyclesInSubgraph.empty()
    val cycle2 = new Cycle(java.util.Arrays.asList(refB, refC))
    other.add(cycle2)
    other.rememberPackageAsInvestigated(packageB)

    cyclesInSubgraph.mergeIn(other)

    assertEquals(2, cyclesInSubgraph.cycles.size)
    assertEquals(2, cyclesInSubgraph.investigatedPackages.size)
    assertTrue(cyclesInSubgraph.investigatedPackages.contains(packageA))
    assertTrue(cyclesInSubgraph.investigatedPackages.contains(packageB))
  }

  @Test
  def testToString(): Unit = {
    val refA = new PackageReference("org.example.a")
    val refB = new PackageReference("org.example.b")
    val cycle = new Cycle(java.util.Arrays.asList(refA, refB))

    cyclesInSubgraph.add(cycle)
    cyclesInSubgraph.rememberPackageAsInvestigated(packageA)

    val str = cyclesInSubgraph.toString()
    assertTrue(str.contains("CyclesInSubgraph"))
    assertTrue(str.contains("cycles="))
    assertTrue(str.contains("investigatedPackages="))
  }

  private def createTestPackage(name: String): Package = {
    new Package(name) {
      override def isUnreferred(): Boolean = false
      override def getMetrics(): PackageMetrics = PackageMetrics.UNDEFINED
      override def getOwnPackageReferences(): java.util.Set[PackageReference] = java.util.Collections.emptySet[PackageReference]()
      override def getOwnExternalPackageReferences(): java.util.Set[PackageReference] = java.util.Collections.emptySet[PackageReference]()
    }
  }
}

