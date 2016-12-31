package org.tindalos.principle.domain.core.logging

trait ScalaLogger {

  def info(msg: String)
  def error(msg: String)
}

object TheLogger {

  var logger: ScalaLogger = _
  
  def setLogger(aLogger:ScalaLogger) = {
    logger = aLogger
  }

  def info(msg: String) = logger.info(msg)
  def error(msg: String) = logger.error(msg)
}