package ru.softband.logic.mail

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 03.06.2016 at 05:19.
  */

trait Mailer {
    def sendRawMail(from: String, recipients: List[String], subject: String, contents: String)
    def sendHTMLMail(from: String, recipients: List[String], subject: String, contents: NodeSeq)
}
