package uk.co.turingatemyhamster.shoppinglist.shoppinglist.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable

/**
  *
  *
  * @author Matthew Pocock
  */
object ShoppingListSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer[CreateList],
    JsonSerializer[GetList],
    JsonSerializer[ShareList],
    JsonSerializer[AddItem],
    JsonSerializer[ListCreated],
    JsonSerializer[ListShared],
    JsonSerializer[ItemAdded],
    JsonSerializer[ShoppingListState],
    JsonSerializer[ShoppingListAndShares]
  )
}
