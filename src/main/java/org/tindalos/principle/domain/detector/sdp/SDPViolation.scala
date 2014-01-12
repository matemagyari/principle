package org.tindalos.principle.domain.detector.sdp

import org.tindalos.principle.domain.core.Package

case class SDPViolation(val depender:Package, val dependee:Package)