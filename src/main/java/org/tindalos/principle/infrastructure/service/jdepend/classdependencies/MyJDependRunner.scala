package org.tindalos.principle.infrastructure.service.jdepend.classdependencies

import java.io.File

import org.tindalos.principle.domain.detector.structure.Structure.Node
import org.tindalos.principle.domain.detector.structure.{PackageCohesionModule, StructureFinder}
import org.tindalos.principle.domain.util.ListConverter

object MyJDependRunner {

  case class Clazz1(name: String, dependencies: Set[String])

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def toClazz(f: File, rootPackage: String) = {
    val jc = new MyClassFileParser(rootPackage).parse(f)
    Clazz1(jc.getName(), ListConverter.convert(jc.getDependencies()) - jc.getName)
  }

  def addDependants(classes: Set[Clazz1]) =
    for {
      c <- classes
      dependants = classes.filter(x => x.dependencies.contains(c.name))
    } yield Node(c.name, c.dependencies, dependants.map(_.name))

  def getClassesWithInOutDeps(rootPackage: String): Set[Node] = {

    val targetDir: String = "//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-player-reputation-system/target/classes/"
    val rootDir: File = new File(targetDir + rootPackage.replaceAll("\\.", "/"))
    def isNormalClass(f: File) = f.getName.endsWith(".class") && !f.getName.contains("$")
    val classFiles = recursiveListFiles(rootDir).filter(isNormalClass)

    addDependants(classFiles.map(toClazz(_, rootPackage)).toSet)
  }

  def findComponents(rootPackage: String) = {
    //.map(classToComponent)
    val classes = getClassesWithInOutDeps(rootPackage)
    val pc = PackageCohesionModule.packageCohesions(classes)
    val initialComponents = classes.map(StructureFinder.nodeToComponent)
    val components = StructureFinder.collapseToLimit(initialComponents, classes).toList.sortBy(_.nodes.size).reverse
    //val cohs = components.toList.map(c => (c,c.cohesion(classes)))
    components
  }


}
