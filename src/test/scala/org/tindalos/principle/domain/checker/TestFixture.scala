package org.tindalos.principle.domain.checker

import org.tindalos.principle.utils.logging.{SimpleLogger, TheLogger}

object TestFixture {

  def setLogger() = {
    TheLogger.setLogger(new SimpleLogger() {
      override def info(msg: String) = {
        System.out.println(msg)
      }

      override def error(msg: String) = {
        System.err.println(msg)
      }
    })
  }

}