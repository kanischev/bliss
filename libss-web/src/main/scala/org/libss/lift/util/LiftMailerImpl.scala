package org.libss.lift.util

import java.io.StringWriter

import com.google.inject.Inject
import net.liftweb.util.{Html5, Mailer}
import net.liftweb.util.Mailer.{From, PlainPlusBodyType, Subject, To, XHTMLMailBodyType}
import org.libss.lift.boot.LibssLocalizable
import org.libss.logic.guice.SafeInjection
import org.libss.logic.i18n.{InjectedLocalizable, LocaleProvider, Localizable}
import org.libss.logic.mail.MailHelper
import org.libss.util.Loggable

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 12.06.2016 at 02:09.
  */
class LiftMailerImpl extends MailHelper {
  def sendRawMail(from: String, recipients: List[String], subject: String, contents: String) {
    Mailer.sendMail(From(from),
      Subject(subject),
      PlainPlusBodyType(contents, "UTF-8") :: recipients.map(To(_)) : _*)
  }

  def sendHTMLMail(from: String, recipients: List[String], subject: String, contents: NodeSeq) {
    Mailer.sendMail(From(from),
      Subject(subject),
      XHTMLMailBodyType(contents) :: recipients.map(To(_)) : _*)
  }
}

class LiftLogMailerImpl
  extends MailHelper
    with Loggable
    with LibssLocalizable {

  protected val printingTemplate =
    s"""---------------------------------------------------
      | ${i18n("mail.send.title.label")}
      | ${i18n("mail.send.from.label")}: %s
      | ${i18n("mail.send.to.label")}: %s
      | ${i18n("mail.send.subject.label")}: %s
      | ${i18n("mail.send.contents.label")}:
      | %s
      |===================================================
    """.stripMargin

  def sendRawMail(from: String, recipients: List[String], subject: String, contents: String) {
    Logger.info(printingTemplate.format(from, recipients.mkString("", ", ", ""), subject, contents))
  }

  def sendHTMLMail(from: String, recipients: List[String], subject: String, contents: NodeSeq) {
    val strWrt = new StringWriter()
    contents.foreach(n => Html5.write(n, strWrt, stripComment = false, convertAmp = true))
    strWrt.close()
    Logger.info(printingTemplate.format(from, recipients.mkString("", ", ", ""), subject, strWrt.toString))
  }
}