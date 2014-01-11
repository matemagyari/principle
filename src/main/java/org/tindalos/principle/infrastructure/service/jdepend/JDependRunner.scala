package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework.JavaPackage
import jdepend.framework.PackageFilter
import java.io.IOException
import jdepend.framework.JDepend
import org.tindalos.principle.domain.core.ListConverter
import java.util.Collection

class JDependRunner {

  def getAnalyzedPackages(basePackage:String):List[JavaPackage] = {
        
        try {
            val jDepend = new JDepend();
            val directory = "./target/classes/"+basePackage.replaceAll("\\.", "/");
			jDepend.addDirectory(directory);
			val filter = new PackageFilter();
			filter.accept(basePackage);
            jDepend.setFilter(filter);
			
            jDepend.addPackage(basePackage);
            val packageCollection:Collection[JavaPackage] = jDepend.analyze().asInstanceOf[Collection[JavaPackage]]
            ListConverter.convert(packageCollection)
        } catch {
          case ex : IOException => throw new ClassesToAnalyzeNotFoundException(ex)
        }
    }
}