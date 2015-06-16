package org.tindalos.principle.domain.resultprocessing.reporter

trait Printer {
  
  	def printInfo(text:String)
  	def printWarning(text:String)

}