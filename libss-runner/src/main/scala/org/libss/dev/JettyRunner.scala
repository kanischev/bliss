package org.libss.dev

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.xml.XmlConfiguration

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