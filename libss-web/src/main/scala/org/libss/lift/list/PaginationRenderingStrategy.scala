package org.libss.lift.list

import net.liftweb.util.CssSel
import org.libss.lift.boot.LibssLocalizable
import org.libss.logic.i18n.Localizable
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

/**
  * Created by Kaa 
  * on 22.06.2016 at 00:40.
  */
trait PaginationRenderingStrategy {
  /**
    * if not specified no link to begin rendered ever
    */
  def toBeginLabel: Option[String]

  /**
    * if not specified no link to end rendered ever
    */
  def toEndLabel: Option[String]

  /**
    * if not specified no link to previous page rendered ever
    */
  def toPreviousLabel: Option[String]

  /**
    * if not specified no link to next page rendered ever
    */
  def toNextLabel: Option[String]

  def alwaysRenderBeginEnd: Boolean

  /**
    * Handle previous and start page links rendering
    *
    * @param totalPageCount total pages in data being rendered
    * @param page current
    * @param pageReferenceRenderer function that render page link
    * @return NodeSeq for rendered Begin and Previous buttons
    */
  def renderToBeginAndPrevious(totalPageCount: Long,
                               page: Long,
                               pageReferenceRenderer: (Long,  Option[String]) => NodeSeq): NodeSeq

  /**
    * Handle next and toEnd page links rendering
    *
    * @param totalPageCount total pages in data being rendered
    * @param page current
    * @param pageReferenceRenderer function that renders page link
    * @return NodeSeq for rendered Next and Last buttons
    */
  def renderToEndAndNext(totalPageCount: Long,
                         page: Long,
                         pageReferenceRenderer: (Long,  Option[String]) => NodeSeq): NodeSeq

  def renderPagesBlock(totalPageCount: Long,
                       page: Long,
                       pageReferenceRenderer: (Long,  Option[String]) => NodeSeq): NodeSeq

  def bindPaginationRendering(totalPageCount: Long,
                              page: Long,
                              pageReferenceRenderer: (Long,  Option[String]) => NodeSeq): CssSel

  def renderPagination(totalPageCount: Long,
                       page: Long,
                       pageReferenceRenderer: (Long,  Option[String]) => NodeSeq): NodeSeq
}

trait AbstractPaginationRenderingStrategy
  extends PaginationRenderingStrategy
  with LibssLocalizable {

  /**
    * @inheritdoc
    */
  override def bindPaginationRendering(totalPageCount: Long, page: Long, pageReferenceRenderer: (Long, Option[String]) => NodeSeq) = {
    ".pagination" #> {
        renderToBeginAndPrevious(totalPageCount, page, pageReferenceRenderer) ++
        renderPagesBlock(totalPageCount, page, pageReferenceRenderer) ++
        renderToEndAndNext(totalPageCount, page, pageReferenceRenderer)
    }
  }
  /**
    * @inheritdoc
    */
  override def renderPagination(totalPageCount: Long, page: Long, pageReferenceRenderer: (Long, Option[String]) => NodeSeq) = {
    bindPaginationRendering(totalPageCount, page, pageReferenceRenderer)(<ul class="pagination"></ul>)
  }

  /**
    * @inheritdoc
    */
  override def renderToEndAndNext(totalPageCount: Long, page: Long, pageReferenceRenderer: (Long, Option[String]) => NodeSeq) = {
    {
      if (page < totalPageCount && toNextLabel.isDefined) <li>{pageReferenceRenderer(page + 1, toNextLabel)}</li>
      else if (alwaysRenderBeginEnd && toNextLabel.isDefined) <li class="disabled">{pageReferenceRenderer(totalPageCount, toNextLabel)}</li>
      else NodeSeq.Empty
    } ++ {
      if (page < totalPageCount && toEndLabel.isDefined) <li>{pageReferenceRenderer(totalPageCount, toEndLabel)}</li>
      else if (alwaysRenderBeginEnd && toEndLabel.isDefined) <li class="disabled">{pageReferenceRenderer(totalPageCount, toEndLabel)}</li>
      else NodeSeq.Empty
    }
  }

  /**
    * @inheritdoc
    */
  override def renderToBeginAndPrevious(totalPageCount: Long, page: Long, pageReferenceRenderer: (Long, Option[String]) => NodeSeq) = {
    {
      if (page < totalPageCount && toNextLabel.isDefined) <li>{pageReferenceRenderer(page + 1, toNextLabel)}</li>
      else if (alwaysRenderBeginEnd && toNextLabel.isDefined) <li class="disabled">{pageReferenceRenderer(totalPageCount, toNextLabel)}</li>
      else NodeSeq.Empty
    } ++ {
      if (page < totalPageCount && toEndLabel.isDefined) <li>{pageReferenceRenderer(totalPageCount, toEndLabel)}</li>
      else if (alwaysRenderBeginEnd && toEndLabel.isDefined) <li class="disabled">{pageReferenceRenderer(totalPageCount, toEndLabel)}</li>
      else NodeSeq.Empty
    }
  }

  // Localized labels
  override def toBeginLabel: Option[String] = Option(i18n("to.begin.page.label"))
  override def toEndLabel: Option[String] = Option(i18n("to.end.page.label"))
  override def toNextLabel: Option[String] = Option(i18n("next.page.label"))
  override def toPreviousLabel: Option[String] = Option(i18n("previous.page.label"))
}

case class FixedPrePostPagesCount( renderAdditionalCount: Int = 5,
                                   alwaysRenderBeginEnd: Boolean = true) extends AbstractPaginationRenderingStrategy {

  def renderPagesBlock(totalPageCount: Long, page: Long, pageReferenceRenderer: (Long, Option[String]) => NodeSeq) = {
    Range(math.max(page-renderAdditionalCount, 1).toInt, math.min(totalPageCount, page+renderAdditionalCount).toInt+1).foldLeft(NodeSeq.Empty)((ns, v) =>
      if (v == page) ns ++ <li class="active">{pageReferenceRenderer(v, None)}</li>
      else ns ++ <li>{pageReferenceRenderer(v, None)}</li>
    )
  }
}

case class FixedLinksCount(pagesToRender: Int = 5,
                           alwaysRenderBeginEnd: Boolean = true,
                           labelResources: List[String] = Nil
                          ) extends AbstractPaginationRenderingStrategy with Localizable {


  override def resourceNames: List[String] = if (labelResources.isEmpty) super.resourceNames else labelResources

  def renderPagesBlock(totalPageCount: Long, page: Long, pageReferenceRenderer: (Long, Option[String]) => NodeSeq) = {
    val startIndex = {
      if(page-(pagesToRender-1)/2 <= 1) 1
      else if(page+(pagesToRender-1)/2 >= totalPageCount) math.max(1, totalPageCount-pagesToRender + 1)
      else page-(pagesToRender-1)/2
    }

    val endIndex = {
      if(startIndex + pagesToRender - 1 > totalPageCount) totalPageCount
      else startIndex + pagesToRender - 1
    }

    Range(startIndex.toInt, endIndex.toInt+1).foldLeft(NodeSeq.Empty)((ns, v) =>
      if (v == page) ns ++ <li class="active">{pageReferenceRenderer(v, None)}</li>
      else ns ++ <li>{pageReferenceRenderer(v, None)}</li>
    )
  }
}