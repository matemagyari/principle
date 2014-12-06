package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.detector.thirdparty.ThirdPartyViolationsResult

object ThirdPartyViolationsReporter {

  def report(result: ThirdPartyViolationsResult) = {
    val violations = result.violations
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\nThird party violations (" + violations.length + " of allowed " + result.threshold + " )\t")
    sb.append("\n" + sectionLine + "\n")

    if (violations.isEmpty) sb.append("No violations.\n")
    else violations.foreach(violation => sb.append(s"${violation._1} refers to ${violation._2}\n"))
    
    sb.append(sectionLine + "\n")
    sb.toString()
  }

}