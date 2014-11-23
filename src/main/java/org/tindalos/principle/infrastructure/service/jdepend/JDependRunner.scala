package org.tindalos.principle.infrastructure.service.jdepend

import java.io.IOException
import java.util.Collection

import jdepend.framework.{JDepend, JavaPackage, PackageFilter}
import org.tindalos.principle.domain.util.ListConverter

object JDependRunner {

  def getAnalyzedPackagesUnder(basePackage: String, filterEnabled: Boolean = true) = {

    try {
      val jDepend = new JDepend();
      val directory = "./target/classes/" + basePackage.replaceAll("\\.", "/");
      jDepend.addDirectory(directory);
      if (filterEnabled) {
        val filter = new PackageFilter();
        filter.accept(basePackage);
        jDepend.setFilter(filter);
      }

      jDepend.addPackage(basePackage);
      val packageCollection = jDepend.analyze().asInstanceOf[Collection[JavaPackage]]
      ListConverter.convert(packageCollection)
    } catch {
      case ex: IOException => throw new ClassesToAnalyzeNotFoundException(ex)
    }
  }

}