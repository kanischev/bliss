package ru.softband.dev

import scala.xml.NodeSeq
import scala.collection.JavaConversions._

/**
 * date: 09.04.14
 * author: Kaa
 */
case class JettyXml(port: Int,
                    defaultDSName: String,
                    serverName: String = "ProjectServer",
                    defaultMailServiceName: Option[String] = None) {

  def jettyXmlConfig =
    <Configure id={serverName} class="org.eclipse.jetty.server.Server">

      <Call name="addConnector">
        <Arg>
          <New class="org.eclipse.jetty.server.nio.SelectChannelConnector">
            <Set name="port">{port.toString}</Set>
          </New>
        </Arg>
      </Call>

      <Set name="handler">
        <New class="org.eclipse.jetty.server.handler.HandlerList">
          <Set name="handlers">
            <Array type="org.eclipse.jetty.server.Handler">
              <Item>

                <New id="wac" class="org.eclipse.jetty.webapp.WebAppContext">

                  <Set name="descriptor">../src/main/webapp/WEB-INF/web.xml</Set>
                  <Set name="resourceBase">../src/main/webapp</Set>
                  <Set name="contextPath">/</Set>
                  {
                  insertDataSource("db") ++
                    insertMailConfig("mail")
                  }
                  <New id="ProjectDS" class="org.eclipse.jetty.plus.jndi.Resource">
                    <Arg>{defaultDSName}</Arg>
                    <Arg>
                      <Ref id="dbDS"/>
                    </Arg>
                  </New>
                  {
                  if (defaultMailServiceName.isDefined) {
                    <New id="Mail" class="org.eclipse.jetty.plus.jndi.Resource">
                      <Arg>{"java:comp/env/" + defaultMailServiceName.get}</Arg>
                      <Arg>
                        <Ref id="mailSession"/>
                      </Arg>
                    </New>
                  } else NodeSeq.Empty
                  }
                </New>
              </Item>
            </Array>
          </Set>
        </New>
      </Set>
    </Configure>


  private def insertDataSource(prefix: String) = {
    val dbUser = System.getProperty(prefix + ".user")
    val dbUrl = System.getProperty(prefix + ".url")
    val dbPass = Option(System.getProperty(prefix + ".pass"))
    val dsId = prefix + "DS"

    if (dbUser != null)
      mySqlDataSource(dbUser, dbUrl, dsId, dbPass)
    else
      h2DataSource(
        Option(dbUrl).getOrElse("jdbc:h2:mem:" + defaultDSName + ";LOCK_MODE=1"),
        dsId
      )
  }

  private def mySqlDataSource(userName: String, dbUrl: String, dsId: String, pwd: Option[String] = None) =
    dataSource(
      "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource",
      Map(
        "url" -> dbUrl,
        "user" -> userName,
        "password" -> pwd.getOrElse(userName)
      ),
      dsId
    )

  private def oracleDataSource(username: String, dbUrl: String, dsId: String) =
    dataSource(
      "oracle.jdbc.xa.client.OracleXADataSource",
      Map(
        "URL" -> dbUrl, //url must be in upper case because oracle.jdbc.pool.OracleDataSource has method setURL
        "user" -> username,
        "password" -> username
      ),
      dsId
    )

  private def h2DataSource(dbUrl: String, dsId: String) =
    dataSource("org.h2.jdbcx.JdbcDataSource", Map("URL" -> dbUrl), dsId)

  private def dataSource(xaDataSourceClassName: String, properties: Map[String, String], dsId: String) =
    <New id={dsId} class="com.atomikos.jdbc.AtomikosDataSourceBean">
      <Set name="minPoolSize">2</Set>
      <Set name="maxPoolSize">50</Set>
      <Set name="UniqueResourceName">
        {"jdbc/" + dsId.capitalize}
      </Set>
      <Set name="xaDataSourceClassName">
        {xaDataSourceClassName}
      </Set>
      <Get name="xaProperties">
        {insertProperties(properties)}
      </Get>
    </New>

  private def insertProperties(properties: Map[String, String]) =
    properties.foldLeft(NodeSeq.Empty)((seq, t) => insertProperty(t._1, t._2) +: seq)

  private def insertProperty(name: String, value: String) =
    <Call name="setProperty">
      <Arg>
        {name}
      </Arg>
      <Arg>
        {value}
      </Arg>
    </Call>

  private def putProperties(properties: Map[String, String]) =
    <New class="java.util.Properties">
      {properties.foldLeft(NodeSeq.Empty)((seq, t) => putProperty(t._1, t._2) +: seq)}
    </New>

  private def putProperty(propName: String, propValue: String) =
    <Put name={propName}>{propValue}</Put>

  protected def insertMailConfig(prefix: String): Option[NodeSeq] = {
    if (Option(System.getProperty(prefix + ".mail.smtp.host")).isEmpty)
      None
    else {
      val propMap = System.getProperties.propertyNames().filter(_.toString.startsWith(prefix))
        .map(key => key.toString.substring(prefix.length + 1) -> System.getProperty(key.toString))
        .toMap
      Some(mailConfig(prefix,
        propMap.get("user"),
        propMap.get("password"),
        propMap.filterKeys(key => key != "user" && key != "password")))
    }
  }

  private def mailConfig(prefix: String,
                         user: Option[String],
                         pwd: Option[String],
                         propertyMap: Map[String, String]): NodeSeq = {
    <New class="org.eclipse.jetty.plus.jndi.Resource">
      <Arg>{"mail/" + prefix.capitalize + "Session"}</Arg>
      <Arg>
        <New id={prefix+"Session"} class="org.eclipse.jetty.jndi.factories.MailSessionReference">
          {user.map(u => <Set name="user">{u}</Set>).getOrElse(NodeSeq.Empty) ++
          pwd.map(p => <Set name="password">{p}</Set>).getOrElse(NodeSeq.Empty)}
          <Set name="properties">
            {putProperties(propertyMap)}
          </Set>
        </New>
      </Arg>
    </New>
  }
}
