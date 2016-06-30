package org.libss.lift.list

import java.util.Locale

import org.libss.logic.i18n.{DefiniteLocaleProvider, LocaleProvider}
import org.scalatest.{FlatSpec, Matchers}

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 30.06.2016 at 23:21.
  */
class ListStub(var prop: String)

class TableItemsListSnippetSpec extends FlatSpec with Matchers {
  "TableItemsListSnippet" should "use english resource bundles for english locale" in {
    val lst = new TableItemsListSnippet[ListStub] {
      override def columns: Seq[EntityValuePresenter[ListStub]] = Nil
      override def render: NodeSeq = renderNoResults(columns)
      override def localeProvider: LocaleProvider = DefiniteLocaleProvider(Locale.ENGLISH)
    }
    lst.render shouldBe <h2>No data found</h2>
  }

  "TableItemsListSnippet" should "use russian resource bundles for russian locale" in {
    val lst = new TableItemsListSnippet[ListStub] {
      override def columns: Seq[EntityValuePresenter[ListStub]] = Nil
      override def render: NodeSeq = renderNoResults(columns)
      override def localeProvider: LocaleProvider = DefiniteLocaleProvider(new Locale("ru"))
    }
    lst.render shouldBe <h2>Нет данных</h2>
  }

}
