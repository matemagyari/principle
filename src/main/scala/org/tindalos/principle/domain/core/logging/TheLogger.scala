package org.tindalos.principle.domain.core.logging


object TheLogger {

  var logger: SimpleLogger = _
  
  def setLogger(aLogger:SimpleLogger) = {
    logger = aLogger
  }

  def info(msg: String) = logger.info(msg)
  def error(msg: String) = logger.error(msg)
}