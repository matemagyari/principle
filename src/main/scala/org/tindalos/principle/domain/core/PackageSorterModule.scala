package org.tindalos.principle.domain.core

import scala.collection.immutable.Seq

object PackageSorterModule {
  
  def sortByName(packages: Seq[Package], basePackageName:String):Seq[Package] =
	 sortByName(packages).filter(_.reference.startsWith(basePackageName))
  
  def sortByName(packages:Seq[Package]) = packages.sortBy(_.reference.name)

}