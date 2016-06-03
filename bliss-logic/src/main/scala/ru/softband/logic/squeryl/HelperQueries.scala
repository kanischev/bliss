package ru.softband.util.squeryl

import org.squeryl.dsl.TypedExpression
import org.squeryl._
import ru.softband.util.helpers.{OptionHelper, SafeStringOptionHelper}
import CallbackSchema._

/**
 * date: 10.04.2014 21:21
 * author: Kaa
 */

/**
 * Provides some widely used methods to work with items of some type:
 *    1. Getting all the items on some page
 *    2. Counting all the items
 *    3. Saving or updating items
 *    4. Getting item by stringified identifier
 *
 * @tparam T Type of table's entity
 * @tparam K Type of Entity's key
 */
trait HelperQueries[T, K]
  extends SafeStringOptionHelper
  with OptionHelper
  with PrimitiveTypeMode {
  /**
   * Define Squeryl table to work with
 *
   * @return
   */
  def table: Table[T]

  implicit def ked: KeyedEntityDef[T, K]
  implicit def ev: K => TypedExpression[K, _]

  /**
   * Query to get all the items for paging (on some @page with @itemsPerPage)
 *
   * @param page Is 1-based
   * @param itemsPerPage Quantity of items per single page
   * @return List of items on the page with @itemsPerPage items per page quantity
   */
  def itemsList(page: Int, itemsPerPage: Int) = from(table)(a => select(a)).page((page-1)*itemsPerPage, itemsPerPage).toList

  /**
   * @return Total quantity of items in DB
   */
  def countItems = from(table)(a => compute(count)).single.measures.toInt

  /**
   * @param item Entity to be saved or updated
   * @return saved entity
   */
  def saveOrUpdate(item: T) = table.insertOrUpdate(item)

  /**
   * @param strId stringified identifier of entity
   * @return optional entity with specified id
   */
  def itemBy(strId: String): Option[T] = stringToId(strId).flatMap(t => table.lookup(t))

  /**
   * Should be defined in subclasses
 *
   * @param strId stringified identifier
   * @return optional entity
   */
  def stringToId(strId: String): Option[K]
}


trait LongKeyedHelperQueries[T <: LongKeyedEntity] extends HelperQueries[T, Long] {
  implicit def ked = kedForLongKeyedEntity[T]
  override implicit def ev: (Long) => TypedExpression[Long, _] =  longToTE
  override def stringToId(strId: String): Option[Long] = toLongOpt(strId)
}

trait UuidKeyedHelperQueries[T <: UuidKeyedEntity[T]] extends HelperQueries[T, String] {
  implicit def ked = kedForUUIDKeyedEntity[T]
  override implicit def ev: (String) => TypedExpression[String, _] =  stringToTE
  override def stringToId(strId: String): Option[String] = inputStringToOptionClever(strId)
}