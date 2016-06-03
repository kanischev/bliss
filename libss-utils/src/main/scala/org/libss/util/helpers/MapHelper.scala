package org.libss.util.helpers

/**
  * date: 02.06.2016 22:46
  * author: Kaa
  *
  * Map helper to prefix and unprefix transformations of Map[String, _] map keys
  */
trait MapHelper {
  implicit def mapToStringKeyedMap[T](mapp: Map[String, T]) = StringKeyedMap(mapp)

  case class StringKeyedMap[T](mapp: Map[String, T]) {
    def prefixKeysWith(keyPrefix: String) = {
      mapp.map {
        case (k, v) => (k + keyPrefix) -> v
      }
    }

    def unPrefixKeys(keyPart: String) = {
      mapp.map {
        case (k, v) => if (k.startsWith(keyPart)) k.substring(keyPart.length) -> v else k -> v
      }.toMap
    }
  }
}