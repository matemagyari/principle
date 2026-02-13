package org.tindalos.principle.domain.expectations

case class ThirdParty(barriers: List[Barrier] = List.empty, override val violationThreshold: Int = 0) extends IntExpectation

case class SubmodulesBlueprint(location:String, override val violationThreshold:Int = 0) extends IntExpectation

