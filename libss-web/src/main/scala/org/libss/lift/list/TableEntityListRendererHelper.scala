package org.libss.lift.list

import net.liftweb.http.js.JsCmd
import net.liftweb.util.Helpers._
import net.liftweb.util.{CssSel, PassThru}

import scala.xml.{NodeSeq, Text}

 /**
  * Trait that helps in items lists table rendering
  */
trait TableEntityListRenderHelper {
  /**
    * @param cols List of [[EntityValuePresenter]]'s
    * @return Lift CssSel for table header rendering
    */
  def renderTableHeader(cols: Seq[EntityValuePresenter[_]]): CssSel

  /**
    * @param column - The column to get isClickable info
    * @return Logical value representing whether column is clickable
    */
  def isColumnClickable(column: EntityValuePresenter[_]): Boolean

  /**
    * @param item entity to render column value for
    * @param column one of entity value presenters
    * @return CssSel for item value rendering
    */
  def bindToColumn[E](item: E,
                   column: EntityValuePresenter[E],
                   rowClickCmd: Option[(E) => JsCmd]
                  ): CssSel

   /**
     * @param item to be rendered in row
     * @param columns presenters list to be rendered
     * @param rowClickCmd Optional JsCmd to be called on row rendering
     * @param item2RowId Optional item to id conversion
     * @param rowRenderingHook hook for rendering item row - adding row style for example
     * @tparam E rendering entity's type
     * @return Css selector with body-to-render binding
     */
  def bindItemToTableRow[E](item: E,
                            columns: Seq[EntityValuePresenter[E]],
                            rowClickCmd: Option[(E) => JsCmd] = None,
                            item2RowId: Option[(E) => String] = None,
                            rowRenderingHook: Option[(E) => CssSel] = None
                           ): CssSel

  def renderTableBody[E](items: Seq[E], cols: Seq[EntityValuePresenter[E]]): CssSel

  def renderTableInfo(page: Long, itemsPerPage: Long, totalItems: Long): CssSel

   def renderPager(totalPageCount: Long, page: Long, referenceRenderer: (Long, Option[String]) => NodeSeq): CssSel

 }

/**
  * Default twitter-bootstrap styled renderer helper implementation
  */
object BootstrapTableEntityListRenderHelper
  extends TableEntityListRenderHelper {
  /** @inheritdoc */
  override def renderTableHeader(cols: Seq[EntityValuePresenter[_]]): CssSel = ".columnHead *" #> cols.map(_.renderLabel)

  /** @inheritdoc */
  override def isColumnClickable(column: EntityValuePresenter[_]): Boolean = !column.isInstanceOf[TableColumn[_]] || (column.isInstanceOf[TableColumn[_]] && column.asInstanceOf[TableColumn[_]].clickable)

  /** @inheritdoc */
  override def bindToColumn[E](item: E,
                                      column: EntityValuePresenter[E],
                                      rowClickCmd: Option[(E) => JsCmd]) = {
    {
      (if(rowClickCmd.isDefined && isColumnClickable(column)) {
        ".column [onclick]" #> rowClickCmd.get.apply(item).toJsCmd &
          ".column [class+]" #> "clickable"
      } else "*" #> PassThru) &
        ".column *" #> column.renderValue(item)
    } &
      ".column [class+]" #> {column match {
        case aligned: TableColumn[_] => aligned.valueAlignment.toString
        case _ => ""
      }}
  }

  /** @inheritdoc */
  override def bindItemToTableRow[E](item: E,
                                     columns: Seq[EntityValuePresenter[E]],
                                     rowClickCmd: Option[(E) => JsCmd] = None,
                                     item2RowId: Option[(E) => String] = None,
                                     rowRenderingHook: Option[(E) => CssSel] = None
                                 ) =  {
    val t = item2RowId.foldLeft(".column" #> columns.map(c => bindToColumn(item, c, rowClickCmd)).toList)((cssSel, idGenFun) => ".itemRow [id]" #> idGenFun(item) & cssSel)
    rowRenderingHook.fold(t)(_.apply(item) & t)
  }

  /** @inheritdoc */
  override def renderTableBody[E](items: Seq[E], cols: Seq[EntityValuePresenter[E]]): CssSel  = ".itemRow" #> items.map(item => bindItemToTableRow(item, cols)).toList

  //TODO: i18n
  override def renderTableInfo(page: Long, itemsPerPage: Long, totalItems: Long): CssSel = ".tableInfo *" #> Text("Показаны с " + ((page-1)*itemsPerPage + 1) + " по " + (math.min(totalItems, page * itemsPerPage)) + " из " + totalItems)

  override def renderPager(totalPageCount: Long, page: Long, referenceRenderer: (Long, Option[String]) => NodeSeq): CssSel = ".table-pagination *" #> NodeSeq.Empty
}