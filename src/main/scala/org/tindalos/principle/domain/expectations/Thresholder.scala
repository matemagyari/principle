package org.tindalos.principle.domain.expectations


case class SubmodulesBlueprint(location:String, override val violationThreshold:Int = 0) extends IntExpectation

