package org.libss.logic.i18n

import java.util.{ListResourceBundle, ResourceBundle}

import com.google.inject.Inject

import scala.util.Try

/**
  * Created by Kaa 
  * on 09.06.2016 at 00:29.
  *
  * Leave it in logic package because of LocaleProvider injection
  */
trait LocalizedResource {
  @Inject(optional=true)
  protected var localeProvider: LocaleProvider = _

  /**
    * @return Ordered list of paths to try to find resource bundles
    */
  def resourceNames: List[String] =
    this.getClass.getCanonicalName ::               // Resource name same as class name - near or in resources/<package> folder
    s"i18n.${this.getClass.getCanonicalName}" ::    // for resources/i18n/<package> structure
    Nil

  def defaultResourceBundle = new ListResourceBundle() {
    override def getContents: Array[Array[AnyRef]] = Array[Array[AnyRef]]()
  }

  lazy val rb = Option(localeProvider).flatMap(lp => Option(lp.getLocale)).flatMap(l => {
      resourceNames.find(rn => Try(ResourceBundle.getBundle(rn, l)).isSuccess).map(rn => ResourceBundle.getBundle(rn, l))
    }).orElse(resourceNames.find(rn => Try(ResourceBundle.getBundle(rn)).isSuccess).map(rn => ResourceBundle.getBundle(rn)))
    .getOrElse(defaultResourceBundle)

  /**
    * @param property
    * @return Localized message or provided key if message could not be find
    */
  def getProp(property: String) = if (rb.containsKey(property)) rb.getString(property) else property
}

case class DefiniteLocalizedResource(bundlePath: String) extends LocalizedResource {
  /**
    * @return Ordered list of paths to try to find resource bundles
    */
  override def resourceNames: List[String] = bundlePath :: Nil
}
