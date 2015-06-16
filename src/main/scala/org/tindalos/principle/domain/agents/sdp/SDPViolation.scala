package org.tindalos.principle.domain.agents.sdp

import org.tindalos.principle.domain.core.Package

case class SDPViolation(depender:Package, dependee:Package)