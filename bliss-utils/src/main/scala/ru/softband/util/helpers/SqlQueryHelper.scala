package ru.softband.util.helpers

/**
  * date: 02.06.2016 22:44
  * author: Kaa
  *
  * SQL queries helper - implicitly adds to string helpers for like-searches by start | end | full
  */
trait SqlQueryHelper {
  case class SearchString(str: String) {
    def likePrefix = str + "%"
    def likePostfix = "%" + str
    def likePrefPostf = "%" + str + "%"

    def fullLike = "%" + fullLikeSameStart
    def fullLikeSameStart = str.trim.toLowerCase.replaceAll(" ", "%") + "%"
    // TODO: MySQL doesn't properly accept slashes in like (waidw??). I used this method as temporal solution.
    // Should be deleted or to be understood why it's needed...
    def clear = str.replace('\\', '%').replace('/', '%')
  }

  implicit def stringToSearchString(str: String) = SearchString(str)
}
