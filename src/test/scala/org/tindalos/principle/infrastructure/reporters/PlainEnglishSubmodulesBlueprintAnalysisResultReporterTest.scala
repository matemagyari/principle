package org.tindalos.principle.infrastructure.reporters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.tindalos.principle.domain.analyzers.submodulesblueprint.{Overlap, Submodule, SubmoduleId, SubmodulesBlueprintAnalysisResult}
import org.tindalos.principle.domain.core.packages.PackageWithMetrics

class PlainEnglishSubmodulesBlueprintAnalysisResultReporterTest {

  private val SEP = "=============================================================="
  private val reporter = new PlainEnglishSubmodulesBlueprintAnalysisResultReporter()

  private def submodule(id: String): Submodule =
    new Submodule(new SubmoduleId(id), java.util.Set.of[PackageWithMetrics](), java.util.Set.of[SubmoduleId]())

  @Test
  def noViolations_reportsNoViolations(): Unit = {
    val result = SubmodulesBlueprintAnalysisResult.empty(0)

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |Submodules Blueprint violations (0 of the allowed 0)
         |$SEP
         |No violations.
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withOverlap_reportsOverlapMessage(): Unit = {
    val overlap = new Overlap(new SubmoduleId("MOD1"), new SubmoduleId("MOD2"))
    val result = SubmodulesBlueprintAnalysisResult.withOverlaps(0, java.util.Set.of[Overlap](overlap))

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |Submodules Blueprint violations (0 of the allowed 0)
         |$SEP
         |Invalid blueprint definition, overlapping modules
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withIllegalDependency_reportsIllegalDependency(): Unit = {
    val mod1 = submodule("MOD1")
    val mod2 = submodule("MOD2")
    val result = SubmodulesBlueprintAnalysisResult.withViolations(0, java.util.Map.of(mod1, java.util.Set.of[Submodule](mod2)), java.util.Map.of[Submodule, java.util.Set[Submodule]]())

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |Submodules Blueprint violations (1 of the allowed 0)
         |$SEP
         |Illegal dependency: MOD1 -> Set(MOD2)
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withMissingDependency_reportsMissingDependency(): Unit = {
    val mod1 = submodule("MOD1")
    val mod2 = submodule("MOD2")
    val result = SubmodulesBlueprintAnalysisResult.withViolations(0, java.util.Map.of[Submodule, java.util.Set[Submodule]](), java.util.Map.of(mod1, java.util.Set.of[Submodule](mod2)))

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |Submodules Blueprint violations (1 of the allowed 0)
         |$SEP
         |Missing dependency: MOD1 -> Set(MOD2)
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }

  @Test
  def withIllegalAndMissingDependencies_reportsBoth(): Unit = {
    val mod1 = submodule("MOD1")
    val mod2 = submodule("MOD2")
    val mod3 = submodule("MOD3")
    val result = SubmodulesBlueprintAnalysisResult.withViolations(
      0,
      java.util.Map.of(mod1, java.util.Set.of[Submodule](mod2)),
      java.util.Map.of(mod1, java.util.Set.of[Submodule](mod3))
    )

    val report = reporter.report(result)

    val expected =
      s"""
         |$SEP
         |Submodules Blueprint violations (2 of the allowed 0)
         |$SEP
         |Illegal dependency: MOD1 -> Set(MOD2)
         |Missing dependency: MOD1 -> Set(MOD3)
         |$SEP
         |""".stripMargin
    assertEquals(expected, report)
  }
}
