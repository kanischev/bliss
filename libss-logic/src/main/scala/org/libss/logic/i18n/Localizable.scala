package org.libss.logic.i18n

import java.util.{ListResourceBundle, ResourceBundle}

import com.google.inject.Inject
import org.libss.logic.guice.{Injection, SafeInjection}

import scala.collection.JavaConversions._
import scala.util.Try

/**
  * Created by Kaa 
  * on 09.06.2016 at 00:29.
  *
  * Leave it in logic package because of LocaleProvider injection
  */
trait Localizable {
  def localeProvider: LocaleProvider

  protected def forClass(clazz: Class[_]) = clazz.getCanonicalName :: s"i18n.${clazz.getCanonicalName}" :: Nil

  /**
    * All properties from different resource bundles are combined to single map - so properties
    * can be overriden by following resource bundles contents
    * @return Ordered list of paths to try to find resource bundles one after another.
    */
  def resourceNames: List[String] = forClass(this.getClass)

  def defaultResourceBundle = new ListResourceBundle() {
    override def getContents: Array[Array[AnyRef]] = Array[Array[AnyRef]]()
  }

  lazy val rb = Option(localeProvider).flatMap(lp => Option(lp.getLocale))
    .map(locale =>
      resourceNames.find(rn => Try(ResourceBundle.getBundle(rn, locale)).isSuccess).map(rn => ResourceBundle.getBundle(rn, locale))
    ).getOrElse(
      resourceNames.find(rn => Try(ResourceBundle.getBundle(rn)).isSuccess).map(rn => ResourceBundle.getBundle(rn))
    ).foldLeft(Map.empty[String, String])((m, rb) => m ++ rb.keySet().map(key => key -> rb.getString(key)).toMap)

  /**
    * @param property
    * @return Localized message or provided key if message could not be find
    */
  def getString(property: String) = rb.getOrElse(property, property)
}

trait InjectedLocalizable extends Localizable with SafeInjection {
  @Inject(optional=true)
  var localeProvider: LocaleProvider = _
}

case class DefiniteLocalizable(bundlePath: List[String], localeProviderOpt: Option[LocaleProvider] = None) extends Localizable {
  val localeProvider: LocaleProvider = localeProviderOpt.orNull

  /**
    * @return Ordered list of paths to try to find resource bundles
    */
  override def resourceNames = bundlePath
}

object DefiniteLocalizable {
  def apply(singleRB: String): DefiniteLocalizable = apply(singleRB :: Nil)
}
