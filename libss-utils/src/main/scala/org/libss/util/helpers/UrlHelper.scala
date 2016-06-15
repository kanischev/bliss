package org.libss.util.helpers

/**
  * date: 02.06.2016 23:05
  * author: Kaa
  *
  * URL Helpers trait to provide clear way of getting url with parameters
  */
trait UrlHelper {
  def urlByFullParams(urlParts: Seq[String], params: Map[String, List[String]] = Map.empty[String, List[String]]) = {
    urlParts.mkString("/") +
      {if(params.nonEmpty) params.flatMap{case (k, v) => v.map(vv => k + "=" + vv)}.mkString("?", "&", "")
      else ""}
  }

  def urlBy(urlParts: Seq[String], params: Map[String, String] = Map.empty[String, String]) = {
    urlParts.mkString("/") +
      {if(params.nonEmpty) params.map{case (k, v) => k + "=" + v}.mkString("?", "&", "")
      else ""}
  }

  def url(parts: String*) = urlBy(parts.toSeq)

  def absUrlBy(urlParts: Seq[String], params: Map[String, String] = Map.empty[String, String]) = "/" + urlBy(urlParts, params)

  def absUrl(parts: String*) = absUrlBy(parts.toSeq)

  def pathToParts(urlPath: String) = urlPath.split('/').toList

  protected def prefixWithSplitterIfNeeded(path: String) =
    if (path.startsWith("/") || path.trim.isEmpty) path else "/" + path

}
