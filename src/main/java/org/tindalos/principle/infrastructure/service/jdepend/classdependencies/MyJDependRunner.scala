package org.tindalos.principle.infrastructure.service.jdepend.classdependencies

import java.io.File

import org.tindalos.principle.domain.agents.structure.Graph.Node
import org.tindalos.principle.domain.util.ListConverter

object MyJDependRunner {

  private case class Clazz1(name: String, dependencies: Set[String])

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

  //to ignore inner subclasses
  def className(fullName:String) =
    if (fullName.contains("$")) fullName.substring(0, fullName.indexOf("$"))
    else fullName

  def createNodesOfClasses(rootPackage: String, targetDir: String = "./target/classes/"): Set[Node] = {

    val rootDir = new File(targetDir + rootPackage.replaceAll("\\.", "/"))
    //from the 'groupBy' it's needed to handle inner classes
    val clazzes =
      recursiveListFiles(rootDir) //Array[File]
      .filter(_.getName.endsWith(".class"))
      .map(toClazz(_, rootPackage)) //Array[Clazz1]
      .groupBy(c => className(c.name)) //Map[String,Array[Clazz1]]
      .map(kv => {
        val aggregatedDependencies = kv._2.flatMap(_.dependencies).toSet
        Clazz1(kv._1, aggregatedDependencies)
      })

    clazz1sToNodes(clazzes.to[Set])
  }

}
