package org.libss.lift.list

import net.liftweb.http.js.JsCmd
import net.liftweb.util.Helpers._
import net.liftweb.util.{CssSel, PassThru}
import org.libss.lift.boot.LibssRules
import org.libss.logic.i18n.{LocaleProvider, Localizable}

import scala.xml.{NodeSeq, Text}

 /**
  * Trait that helps in items lists table rendering
  */
trait TableEntityListRenderHelper {
  /**
    * @param cols List of [[EntityValuePresenter]]'s
    * @return Lift CssSel for table header rendering
    */
  def bindTableHeaderRendering(cols: Iterable[EntityValuePresenter[_]]): CssSel

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
                            columns: Iterable[EntityValuePresenter[E]],
                            rowClickCmd: Option[(E) => JsCmd] = None,
                            item2RowId: Option[(E) => String] = None,
                            rowRenderingHook: Option[(E) => CssSel] = None
                           ): CssSel

  /**
     * Defines binding for render a list of items to
     *
     * @param items list of entities to be rendered into table
     * @param cols column descriptions list
     * @tparam E type of entity in list to be rendered to this table
     * @return CssSel as binding onto yable template
     */
  def bindTableBodyRendering[E](items: Iterable[E], cols: Iterable[EntityValuePresenter[E]]): CssSel
 }

/**
  * Default twitter-bootstrap styled renderer helper implementation
  */
object BootstrapTableEntityListRenderHelper
  extends TableEntityListRenderHelper {

  /** @inheritdoc */
  override def bindTableHeaderRendering(cols: Iterable[EntityValuePresenter[_]]): CssSel = ".columnHead *" #> cols.map(_.renderLabel)

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
                                     columns: Iterable[EntityValuePresenter[E]],
                                     rowClickCmd: Option[(E) => JsCmd] = None,
                                     item2RowId: Option[(E) => String] = None,
                                     rowRenderingHook: Option[(E) => CssSel] = None
                                 ) =  {
    val t = item2RowId.foldLeft(".column" #> columns.map(c => bindToColumn(item, c, rowClickCmd)).toList)((cssSel, idGenFun) => ".itemRow [id]" #> idGenFun(item) & cssSel)
    rowRenderingHook.fold(t)(_.apply(item) & t)
  }

  /** @inheritdoc */
  override def bindTableBodyRendering[E](items: Iterable[E], cols: Iterable[EntityValuePresenter[E]]): CssSel  = ".itemRow" #> items.map(item => bindItemToTableRow(item, cols)).toList
}