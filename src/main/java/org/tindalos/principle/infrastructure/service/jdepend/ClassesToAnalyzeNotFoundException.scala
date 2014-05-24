package org.tindalos.principle.infrastructure.service.jdepend

import java.io.IOException

case class ClassesToAnalyzeNotFoundException(ex:IOException)
	extends RuntimeException("/target/classes not found! " + ex.getMessage()) 