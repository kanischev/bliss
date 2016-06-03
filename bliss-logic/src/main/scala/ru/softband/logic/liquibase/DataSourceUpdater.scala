package ru.softband.util.liquibase

import javax.sql.DataSource
import java.sql.Connection
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection

/**
  * date: 02.06.2016 23:47
  * author: Kaa
  *
  * Liquibase usage helpers
  * assumes scripts to be in the classpath and to be droped in folders with dsNames and have there scripts.cml file
  * with migrations descriptions
  */
trait DataSourceUpdater {
  def update(dsWithNames: Seq[(String, DataSource)])
}

class LiquibaseInstaller extends DataSourceUpdater {
  /* Override to control contexts liquibase updates executions */
  def liquibaseContexts(dsName: String) = {
    ""
  }

  def withConnection[T](ds: DataSource, fun: (Connection) => T) = {
    fun(ds.getConnection)
  }

  def update(dsWithNames: Seq[(String, DataSource)]) {
    dsWithNames.map{case (dbName, ds) => {
      val fileOpener = new ClassLoaderResourceAccessor(getClass.getClassLoader)

      val liqui = withConnection(ds, (conn) => {
        new Liquibase(dbName + "/scripts.xml", fileOpener, new JdbcConnection(conn))
      })
      liqui.update(liquibaseContexts(dbName))
    }}
  }
}