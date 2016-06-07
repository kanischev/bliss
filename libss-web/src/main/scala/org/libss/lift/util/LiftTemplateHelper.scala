package org.libss.lift.util

import java.util.Locale

import net.liftweb.http.Templates

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 08.06.2016 at 02:17.
  */
trait LiftTemplateHelper {
  def templateBy(name: List[String]) = Templates(name).openOrThrowException(s"No template found with name: ${name.mkString("/")}")

  def templateBy(name: List[String], locale: Locale) = Templates(name, locale).openOrThrowException(s"No template found with name: ${name.mkString("/")} and locale ${locale.toString}")
}

trait TemplateAware {
  def template: NodeSeq
}

trait NamedTemplateAware extends TemplateAware with LiftTemplateHelper {
  def templateName: List[String]

  def localeOpt: Option[Locale] = None

  override def template: NodeSeq = localeOpt.map(templateBy(templateName, _)).getOrElse(templateBy(templateName))
}