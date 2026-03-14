package org.tindalos.principle.domain.core

import org.tindalos.principle.domain.core.packages.PackageWithMetrics

object PackageSorterModule {
  
  def sortByName[T <: PackageWithMetrics](packages:List[T], basePackageName:String):List[T] =
	 sortByName(packages).filter(_.reference.startsWith(basePackageName))
  
  def sortByName[T <: PackageWithMetrics](packages:List[T]): List[T] = packages.sortBy(_.reference.name)

}