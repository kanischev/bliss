package org.libss.lift.guice

import com.google.inject.{AbstractModule, Scopes}
import net.liftweb.util.Props
import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.util.ResourceLoader
import org.libss.lift.form.{CustomBootstrapFormValidation, FormValidation}
import org.libss.lift.util._
import org.libss.logic.guice.ExtendedAbstractModule
import org.libss.logic.i18n.LocaleProvider
import org.libss.logic.liquibase.{DataSourceUpdater, LiquibaseInstaller}
import org.libss.logic.mail.MailHelper
import org.libss.logic.templating.StringToXmlParser

/**
  * Created by Kaa 
  * on 12.06.2016 at 01:40.
  */
class LibssModule extends ExtendedAbstractModule {
  def configureDBInstaller(): Unit = {
    bind(classOf[DataSourceUpdater]) to classOf[LiquibaseInstaller]
  }

  def configureFormValidator(): Unit = {
    bind(classOf[FormValidation]) to classOf[CustomBootstrapFormValidation]
  }

  def configureMailer(): Unit = {
    if (Props.productionMode)
      bind(classOf[MailHelper]) to classOf[LiftMailerImpl] in Scopes.SINGLETON
    else
      bind(classOf[MailHelper]) to classOf[LiftLogMailerImpl] in Scopes.SINGLETON
  }

  def configureLocaleProvider(): Unit = {
    bind(classOf[LocaleProvider]) to classOf[LiftLocaleProvider] in Scopes.SINGLETON
  }

  def configure() {
    configureDBInstaller()
    configureFormValidator()
    configureMailer()
    configureLocaleProvider()
    bind(classOf[StringToXmlParser]) to classOf[StringToXmlParserImpl] in Scopes.SINGLETON
  }
}

class ScalateModule extends AbstractModule {
  protected def mailerBindings() {
    bind(classOf[ResourceLoader]) toInstance LiftResourceLoader()
    bind(classOf[TemplateEngine]) to classOf[LiftTemplateEngine] in Scopes.SINGLETON
  }

  def configure() {
    mailerBindings()
  }
}