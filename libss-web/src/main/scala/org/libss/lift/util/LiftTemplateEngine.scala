package org.libss.lift.util

import java.io.File
import java.net.MalformedURLException
import javax.inject.Inject

import net.liftweb.http.LiftRules
import net.liftweb.http.provider.servlet.HTTPServletContext
import org.fusesource.scalate.layout.DefaultLayoutStrategy
import org.fusesource.scalate.util.Resource._
import org.fusesource.scalate.util.{ClassPathBuilder, FileResourceLoader, ResourceLoader, ResourceNotFoundException}
import org.fusesource.scalate.{Binding, DefaultRenderContext, TemplateEngine}
import org.libss.util.Loggable

import scala.tools.nsc.Global

/**
  * Created by Kaa 
  * on 12.06.2016 at 02:01.
  */
// Copy-pasted from Lift-modules because of lift-2.3-SNAPSHOT dependencies
class LiftTemplateEngine @Inject()(injectedResourceLoader: ResourceLoader) extends TemplateEngine(List(new File(LiftTemplateEngine.realPath("/"))), "production") with Loggable {

  bindings = List(Binding("context", classOf[DefaultRenderContext].getName, importMembers = true, isImplicit = true))

  if (useWebInfWorkingDirectory) {
    val path = LiftTemplateEngine.realPath("WEB-INF")
    if (path != null) {
      workingDirectory = new File(path, "_scalate")
      workingDirectory.mkdirs
    }
  }

  classpath = buildClassPath()
  resourceLoader = injectedResourceLoader
  layoutStrategy = new DefaultLayoutStrategy(this, "/WEB-INF/scalate/layouts/default.scaml", "/WEB-INF/scalate/layouts/default.ssp")

  private def buildClassPath(): String = {
    val builder = new ClassPathBuilder

    // Add containers class path
    builder.addPathFrom(getClass)
      .addPathFrom(classOf[TemplateEngine])
      .addPathFrom(classOf[Product])
      .addPathFrom(classOf[Global])

    // Always include WEB-INF/classes and all the JARs in WEB-INF/lib just in case
    builder.addClassesDir(LiftTemplateEngine.realPath("/WEB-INF/classes"))
      .addLibDir(LiftTemplateEngine.realPath("/WEB-INF/lib"))


    builder.classPath.split(";").filterNot(t => t.startsWith("C:\\Users\\Kaa\\AppData\\Local\\Temp\\classpath") || t.contains("IntelliJ")).map(t => if (t.startsWith("C:\\Users\\Kaa\\AppData\\Local\\Temp\\file:\\")) t.substring("C:\\Users\\Kaa\\AppData\\Local\\Temp\\file:\\".length) else t).mkString(";")
    //    builder.classPath
  }

  def useWebInfWorkingDirectory = {
    val customWorkDir = System.getProperty("scalate.workingdir", "")
    val property = System.getProperty("scalate.temp.workingdir", "")
    println("using scalate.temp.workingdir: " + property)
    property.toLowerCase != "true" && customWorkDir.length <= 0
  }
}

object LiftTemplateEngine extends Loggable {
  def realPath(uri: String): String = {
    LiftRules.context match {
      case http: HTTPServletContext => http.ctx.getRealPath(uri)
      case c => {
        Logger.warn(s"Do not know how to get the real path of: $uri for context: $c")
        uri
      }
    }
  }
}

case class LiftResourceLoader(defaultFolders: Seq[String] = Seq("/WEB-INF/scalate/"))
  extends ResourceLoader
    with Loggable {
  val resLdr = new FileResourceLoader(defaultFolders.map(toFile(_)))

  def resource(uri: String) = {
    val file = realFile(uri)
    if (file != null) {
      if( file.isFile )
        Some(fromFile(file))
      else
        None
    }
    else {
      try {
        val url = LiftRules.context.resource(uri)
        if (url!=null) {
          val resource = fromURL(url)
          Some(resource)
        } else {
          resLdr.resource(uri)
        }
      } catch {
        case x:MalformedURLException=>
          resLdr.resource(uri)
      }
    }

  }

  protected def toFile(uri: String): File = {
    val file = realFile(uri)
    if (file == null) {
      throw new ResourceNotFoundException(resource = uri, root = LiftTemplateEngine.realPath("/"), description = "No file found!")
    }
    file
  }

  /**
    * Returns the real path for the given uri
    */
  def realPath(uri: String): String = {
    val file = realFile(uri)
    if (file != null) file.getPath else null
  }

  /**
    * Returns the File for the given uri
    */
  def realFile(uri: String): File = {
    def findFile(uri: String): File = {
      val path = LiftTemplateEngine.realPath(uri)
      Logger.debug(s"realPath for: $uri is: $path")

      var answer: File = null
      if (path != null) {
        val file = new File(path)
        Logger.debug(s"file from realPath for: $uri is: $file")
        if (file.canRead) {answer = file}
      }
      answer
    }

    findFile(uri) match {
      case file: File => file
      case _ =>
        defaultFolders
          .find(fld => findFile(fld + uri) != null)
          .map(fld => findFile(fld + uri)).orNull
    }
  }

}

