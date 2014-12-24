package org.tindalos.principle.infrastructure.service.jdepend.classdependencies

import java.io.File

import org.tindalos.principle.domain.detector.structure.{StructureFinder, PackageCohesionModule}
import org.tindalos.principle.domain.detector.structure.Structure.{NodeGroup, Node}
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

  def clazz1sToNodes(classes: Set[Clazz1]) =
    for {
      c <- classes
      dependants = classes.filter(x => x.dependencies.contains(c.name))
    } yield Node(c.name, c.dependencies, dependants.map(_.name))

  def createNodesOfClasses(rootPackage: String, targetDir: String = "./target/classes/"): Set[Node] = {

    val rootDir: File = new File(targetDir + rootPackage.replaceAll("\\.", "/"))
    def isNormalClass(f: File) = f.getName.endsWith(".class") && !f.getName.contains("$")
    val classFiles = recursiveListFiles(rootDir).filter(isNormalClass)

    clazz1sToNodes(classFiles.map(toClazz(_, rootPackage)).toSet)
  }

  def findComponents(rootPackage: String, targetDir: String = "./target/classes/") = {
    val classes = createNodesOfClasses(rootPackage, targetDir)
    val pc = PackageCohesionModule.packageCohesions(classes)

    val initialComponents = classes.map(n => NodeGroup(Set(n)))
    StructureFinder.collapseToLimit(initialComponents).toList.sortBy(_.nodes.size).reverse
  }


}
