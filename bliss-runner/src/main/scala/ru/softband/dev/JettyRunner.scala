package ru.softband.dev

import org.eclipse.jetty.xml.XmlConfiguration
import org.eclipse.jetty.server.Server

/**
 * date: 09.04.14
 * author: Kaa
 */

trait JettyRunner {
  def getXmlConfig: JettyXml

  def runServer() {
    val configuration = new XmlConfiguration(getXmlConfig.jettyXmlConfig.toString)
    val server = configuration.configure().asInstanceOf[Server]

    server.start()
    server.join()
  }
}