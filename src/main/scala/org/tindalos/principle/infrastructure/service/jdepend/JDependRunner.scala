package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework._
import scala.collection.JavaConverters._

object JDependRunner {

  def preparePackages(rootPackage: String, filterEnabled: Boolean = true): List[JavaPackage] = {

    val jDepend = new JDepend()
    val directory = "./target/classes/" + rootPackage.replaceAll("\\.", "/")
    jDepend.addDirectory(directory)
    if (filterEnabled) {
      val filter = PackageFilter.all()
      filter.accept(rootPackage)
      jDepend.setFilter(filter)
    }

    jDepend.addPackage(rootPackage)
    jDepend.analyze().asScala.to[List]
  }

}