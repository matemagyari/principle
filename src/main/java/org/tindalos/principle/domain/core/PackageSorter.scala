package org.tindalos.principle.domain.core

import scala.collection.JavaConversions._

class PackageSorter {
  
  def sortByName(packages:List[Package], basePackageName:String):List[Package] = 
	 sortByName(packages).filter(_.getReference().startsWith(basePackageName))
  
  def sortByName(packages:List[Package]) = packages.sortBy(_.getReference().name())

}