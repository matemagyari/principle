package org.tindalos.principle.infrastructure.service.jdepend.classdependencies

import java.io.File

import org.tindalos.principle.domain.agents.structure.Graph.Node
import org.tindalos.principle.infrastructure.BuildPathUtils

object MyJDependRunner {

  private case class Clazz1(name: String, dependencies: Set[String])

  private def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  private def toClazz(f: File, rootPackage: String) = {
    val jc = new MyClassFileParser(rootPackage).parse(f)
    import scala.collection.JavaConverters._
    Clazz1(jc.getName(), jc.getDependencies().asScala.to[Set] - jc.getName)
  }

  private def clazz1sToNodes(classes: Set[Clazz1]) =
    classes.map { clazz ⇒
      val dependantNames = classes
          .filter(_.dependencies.contains(clazz.name))
          .map(_.name)
      Node(clazz.name, clazz.dependencies, dependantNames)
    }

  //to ignore inner subclasses
  def className(fullName:String) =
    if (fullName.contains("$"))
      fullName.substring(0, fullName.indexOf("$"))
    else
      fullName

  def createNodesOfClasses(rootPackage: String, targetDir: String = null): Set[Node] = {

    val classesDir = if (targetDir != null) targetDir else BuildPathUtils.getClassesDirectoryForPackage(rootPackage)
    val rootDir = new File(classesDir + rootPackage.replaceAll("\\.", "/"))
    //from the 'groupBy' it's needed to handle inner classes
    val clazzes =
      recursiveListFiles(rootDir) //Array[File]
      .filter(_.getName.endsWith(".class"))
      .map(toClazz(_, rootPackage)) //Array[Clazz1]
      .groupBy(c => className(c.name)) //Map[String,Array[Clazz1]]
      .map { kv ⇒
        val aggregatedDependencies = kv._2.flatMap(_.dependencies).toSet
        Clazz1(kv._1, aggregatedDependencies)
      }

    clazz1sToNodes(clazzes.to[Set])
  }

}
