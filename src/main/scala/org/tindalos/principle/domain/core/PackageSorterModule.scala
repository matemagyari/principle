package org.tindalos.principle.domain.core

object PackageSorterModule {
  
  def sortByName(packages:List[Package], basePackageName:String):List[Package] = 
	 sortByName(packages).filter(_.reference.startsWith(basePackageName))
  
  def sortByName(packages:List[Package]) = packages.sortBy(_.reference.name)

}