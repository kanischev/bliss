package org.libss.lift.list

import net.liftweb.http.S
import net.liftweb.http.js.JsCmd
import org.libss.lift.boot.LibssRules
import org.libss.lift.util.{NamedTemplateAware, RequestHelper}
import org.libss.logic.i18n.{LocaleProvider, Localizable}
import org.libss.util.PageableBase
import org.libss.util.helpers.{ComputeHelper, MapHelper, OptionHelper}
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 22.06.2016 at 01:48.
  */
/**
  * Main trait for all snippets, rendering lists
  * @tparam E type of entities, that list's data contains
  */
trait TableItemsListSnippet[E]
  extends Localizable
  with NamedTemplateAware {

  def tableRendererHelper: TableEntityListRenderHelper = LibssRules.defaultTableRendererHelper

  override def localeProvider: LocaleProvider = LibssRules.defaultLocaleProvider

  override def templateName: List[String] = LibssRules.defaultTableTemplate

  def items: Seq[E]

  /**
    * @return Seq of columns to be rendered in list
    */
  def columns: Seq[EntityValuePresenter[E]]

  /**
    * @return Command to be executed on row click with item as parameter
    */
  def rowClickCmd: Option[(E) => JsCmd] = None


  /**
    * @inheritdoc
    */
  abstract override def resourceNames: List[String] = super.resourceNames :+ "i18n.org.libss.lift.list.TableItemsList"

  /**
    * Actually render method
    *
    * @return Rendered NodeSeq
    */
  def render: NodeSeq = {
    val genColumns = columns
    val tableItems = items
    if (tableItems.nonEmpty)
      (tableRendererHelper.renderTableHeader(genColumns) &
        tableClassHook(rowClickCmd.isDefined) &
        tableRendererHelper.renderTableBody(items, genColumns))(template)
    else
      renderNoResults(genColumns)
  }

  protected def renderNoResults(columnDescriptions: Seq[EntityValuePresenter[E]]): NodeSeq = <h2>{getString("no.results.label")}</h2>

  def tableClassHook(apply: Boolean) = {
    if(!apply) ".table [class+]" #> ""
    else ".table [class+]" #> "table-hover"
  }
}


trait FilteredTableItemsListSnippet[E, F] extends TableItemsListSnippet[E] {

  /**
    * Override if you need few such snippets on one page
    *
    * @return
    */
  def prefix: Option[String] = None

  /**
    * Should be implemented by API user
    *
    * @return Quantity of items satisfying the filter quantity
    */
  def countAllItems(filter: F): Long

  /**
    * Should be implemented by API user
    *
    * @param filter - filtering object
    * @return should return the list of items satisfying the filter
    */
  def itemsBy(filter: F): Seq[E]

  /**
    * just uses {{itemsBy} with {{defaultFilteringObject}}
    * use itemsBy(filter) instead
    * @return
    */
  override def items: Seq[E] = itemsBy(defaultFilteringObjectFun())

  /**
    * Descriptor for handling filtering form
    *
    * @return typed with filtering object type FilteringDescriptor instance
    */
  def filteringDescriptor:  FilteringDescriptor[F]

  /**
    * Default filtering object creation function
    *
    * @return filtering object with default field values
    */
  def defaultFilteringObjectFun: () => F
}

trait FilteredPageableTableItemsListSnippet[E, F <: PageableBase]
  extends FilteredTableItemsListSnippet[E, F]
  with RequestHelper
  with MapHelper
  with OptionHelper
  with ComputeHelper {

  val PageParamName = "page"
  val ItemsPerPageParamName = "itemsPerPage"

  protected def wrappedWithPrefix(param: String) = prefix.getOrElse("") + param

  /**
    * @inheritdoc
    */
  override def filteringDescriptor: FilteringDescriptor[F]

  /**
    * Change if you need some specific rendering strategy for current list
    * @return Pagination rendering strategy
    */
  def paginationRenderingStrategy: PaginationRenderingStrategy = LibssRules.defaultPaginationRenderingStrategy

  def pageLinkRenderer(value: Long, label: Option[String]): NodeSeq = <a>{label.getOrElse(value.toString)}</a> % ("href" -> withReplacedOrAddedParam(wrappedWithPrefix(PageParamName), List(""+value)))

  /**
    * @inheritdoc
    */
  override def render: NodeSeq = {
    val genColumns = columns
    val reqParamsMap = S.request.toOption.map(_.params).getOrElse(Map.empty[String, List[String]])
    val paramsMap = prefix.map(pr => reqParamsMap.filterKeys(_.startsWith(pr)).unPrefixKeys(pr)).getOrElse(reqParamsMap)
    val descr = filteringDescriptor
    val filtObj = defaultFilteringObjectFun()

    val filterControls = descr.filteringFields
    descr.updateControlsWith(filterControls, filtObj)
    descr.updateFromMap(filterControls, paramsMap.map{case(k, v) => (k, v.head)}.toMap)
    descr.updateFilteringObjectWith(filtObj, filterControls)

    val items = itemsBy(filtObj)
    val itemsQuantity = countAllItems(filtObj)
    val totalPages = divideWithOverflow(itemsQuantity, filtObj.itemsPerPage)
    val page = math.min(filtObj.page, if (totalPages == 0) 1 else totalPages)
    if (itemsQuantity > 0)
      (tableRendererHelper.renderTableHeader(genColumns) &
        tableClassHook(rowClickCmd.isDefined) &
        tableRendererHelper.renderTableBody(items, genColumns) &
        tableRendererHelper.renderPager(totalPages, page, pageLinkRenderer) &
        tableRendererHelper.renderTableInfo(page, filtObj.itemsPerPage, itemsQuantity))(template)
    else
      renderNoResults(genColumns)

  }

  override def templateName: List[String] = LibssRules.defaultPageableTableTemplate
}

