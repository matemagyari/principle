package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework._
import scala.collection.JavaConverters._

import scala.collection.immutable.Seq

object JDependRunner {

  def preparePackages(rootPackage: String, filterEnabled: Boolean = true): Seq[JavaPackage] = {

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