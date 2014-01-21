package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework.JavaPackage
import jdepend.framework.PackageFilter
import java.io.IOException
import jdepend.framework.JDepend
import org.tindalos.principle.domain.util.ListConverter
import java.util.Collection
import org.tindalos.principle.domain.util.ListConverter

class JDependRunner {

  def getAnalyzedPackages(basePackage:String) = {
        
        try {
            val jDepend = new JDepend();
            val directory = "./target/classes/"+basePackage.replaceAll("\\.", "/");
			jDepend.addDirectory(directory);
			val filter = new PackageFilter();
			filter.accept(basePackage);
            jDepend.setFilter(filter);
			
            jDepend.addPackage(basePackage);
            val packageCollection = jDepend.analyze().asInstanceOf[Collection[JavaPackage]]
            ListConverter.convert(packageCollection)
        } catch {
          case ex : IOException => throw new ClassesToAnalyzeNotFoundException(ex)
        }
    }
}