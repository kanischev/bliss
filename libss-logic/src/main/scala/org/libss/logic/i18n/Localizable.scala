package org.libss.logic.i18n

import java.util.concurrent.ConcurrentHashMap
import java.util.{ListResourceBundle, Locale, ResourceBundle}

import com.google.inject.Inject
import org.libss.logic.guice.SafeInjection

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

  val rbCache = new ConcurrentHashMap[String, ConcurrentHashMap[String, String]]()

  def rb = {
    val currentLocale = Option(localeProvider).flatMap(lp => Option(lp.getLocale)).getOrElse(Locale.ROOT)
    val cachedRB = Option(rbCache.get(currentLocale.toString))
    cachedRB.getOrElse({
      val m = new ConcurrentHashMap[String, String]()
      resourceNames.foreach(rn => {
        Try(ResourceBundle.getBundle(rn, currentLocale)).foreach(bundle =>
          bundle.keySet().foreach(key => m.put(key, bundle.getString(key)))
        )
      })
      rbCache.put(currentLocale.toString, m)
      m
    })
  }

  /**
    * @param property the property to get from localization resources
    * @return Localized message or provided key if message could not be find
    */
  def i18n(property: String) = rb.getOrElse(property, property)
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
