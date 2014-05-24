package org.tindalos.principle.domain.detector.sdp

import org.tindalos.principle.domain.core.Package

case class SDPViolation(depender:Package, dependee:Package)