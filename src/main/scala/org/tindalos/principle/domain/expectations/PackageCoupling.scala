package org.tindalos.principle.domain.expectations

//todo - refactor it to a case class
class PackageCoupling(
  var adp: ADP = null,
  var sdp: SDP = null,
  var sap: SAP = null,

  var acd: ACD = null,
  var racd: RACD = null,
  var nccd: NCCD = null,

  var grouping: Grouping = null)

case class Grouping()