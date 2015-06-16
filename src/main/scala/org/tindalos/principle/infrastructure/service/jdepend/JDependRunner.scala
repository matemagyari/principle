package org.tindalos.principle.infrastructure.service.jdepend

import java.util.Collection

import jdepend.framework._
import org.tindalos.principle.domain.util.ListConverter

object JDependRunner {

  def preparePackages(rootPackage: String, filterEnabled: Boolean = true):List[JavaPackage] = {

      val jDepend = new JDepend()
      val directory = "./target/classes/" + rootPackage.replaceAll("\\.", "/")
      jDepend.addDirectory(directory)
      if (filterEnabled) {
        val filter = new PackageFilter()
        filter.accept(rootPackage)
        jDepend.setFilter(filter)
      }

      jDepend.addPackage(rootPackage)
      val packageCollection = jDepend.analyze().asInstanceOf[Collection[JavaPackage]]
      ListConverter.convert(packageCollection)
  }

}