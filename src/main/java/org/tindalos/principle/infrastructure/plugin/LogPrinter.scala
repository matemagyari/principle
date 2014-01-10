package org.tindalos.principle.infrastructure.plugin

import org.tindalos.principle.domain.resultprocessing.reporter.Printer
import org.apache.maven.plugin.logging.Log

class LogPrinter(val log:Log) extends Printer {
  
  	def printInfo(text:String) = log.info(text)
  	def printWarning(text:String) = log.warn(text)

}