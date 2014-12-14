package org.tindalos.principle.domain.core

import scala.collection.JavaConversions._

object PackageSorter {
  
  def sortByName(packages:List[Package], basePackageName:String):List[Package] = 
	 sortByName(packages).filter(_.reference.startsWith(basePackageName))
  
  def sortByName(packages:List[Package]) = packages.sortBy(_.reference.name)

}