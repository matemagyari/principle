package org.tindalos.principle.infrastructure.service.jdepend.classdependencies

import java.io.File

import org.tindalos.principle.domain.detector.structure.Graph.Node
import org.tindalos.principle.domain.util.ListConverter

object MyJDependRunner {

  case class Clazz1(name: String, dependencies: Set[String])

  private def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  private def toClazz(f: File, rootPackage: String) = {
    val jc = new MyClassFileParser(rootPackage).parse(f)
    Clazz1(jc.getName(), ListConverter.convert(jc.getDependencies()) - jc.getName)
  }

  private def clazz1sToNodes(classes: Set[Clazz1]) =
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

}
