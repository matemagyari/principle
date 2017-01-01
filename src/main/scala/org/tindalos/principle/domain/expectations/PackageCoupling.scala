package org.tindalos.principle.domain.expectations

case class PackageCoupling(
  adp: ADP = null,
  sdp: SDP = null,
  sap: SAP = null,
  acd: ACD = null,
  racd: RACD = null,
  nccd: NCCD = null,
  grouping: Grouping = null)

case class Grouping()