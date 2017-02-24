package uk.co.turingatemyhamster.shoppinglist.shoppinglist.impl

import java.time.Instant

import play.api.libs.json.{Format, Json}

/**
  *
  *
  * @author Matthew Pocock
  */
sealed trait ShoppingListEvent


case class ListCreated(listId: String, name: String, ownerId: String, timestamp: Instant = Instant.now()) extends ShoppingListEvent

object ListCreated {
  implicit val format: Format[ListCreated] = Json.format
}


case class ListShared(listId: String, sharedByUserId: String, sharedWithUserId: String, timestamp: Instant = Instant.now()) extends ShoppingListEvent

object ListShared {
  implicit val format: Format[ListShared] = Json.format
}


case class ItemAdded(listid: String, itemId: String, addedByUserId: String, name: String) extends ShoppingListEvent

object ItemAdded {
  implicit val format: Format[ItemAdded] = Json.format
}