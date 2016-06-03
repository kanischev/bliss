package org.libss.logic.squeryl

import org.squeryl.internals.DatabaseAdapter
import org.squeryl.adapters.{MySQLAdapter, MSSQLServer, OracleAdapter, H2Adapter}
import java.sql.{DriverManager, Connection}
import javax.naming.InitialContext
import javax.sql.DataSource
import org.squeryl.Session
import java.util.Properties

/**
 * date: 03.06.2016 00:10
 * author: Kaa
 */

trait SquerylHelper {
  val nameToAdapter = Map[String, DatabaseAdapter](
    "h2" -> new H2Adapter,
    "oracle" -> new OracleAdapter,
    "microsoft" -> new MSSQLServer,
    "mysql" -> new MySQLAdapter
  )

  def getConnection(ds: String): Connection = {
    val dataSource = InitialContext.doLookup[DataSource](ds)
    val connection = dataSource.getConnection
    connection
  }

  protected def createSession(ds: String) = {
    val connection: Connection = getConnection(ds)
    val driverName = connection.getMetaData.getDriverName
    val s = new Session(connection, nameToAdapter.find {
      case (name, adapter) => driverName.toLowerCase.contains(name)
    }.get._2)
    //    s.setLogger(println) //Uncomment for query tracing
    s
  }
}

trait RawConnectionSquerylHelper extends SquerylHelper {
  def username: String
  def password: String
  def connectionString: String

  override def getConnection(ds: String): Connection = {
    val connectionProps = new Properties()
    connectionProps.put("user", username)
    connectionProps.put("password", password)

    DriverManager.getConnection(ds,connectionProps)
  }

  def createSquerylSession() {
    val s = createSession(connectionString)
    s.bindToCurrentThread
  }
}
