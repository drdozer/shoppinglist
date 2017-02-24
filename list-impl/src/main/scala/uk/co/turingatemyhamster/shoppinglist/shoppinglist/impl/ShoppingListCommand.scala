package uk.co.turingatemyhamster.shoppinglist.shoppinglist.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}
import uk.co.turingatemyhamster.shoppinglist.shoppinglist.api.{ShoppingItemId, ShoppingList, ShoppingListId}

/**
  *
  *
  * @author Matthew Pocock
  */
sealed trait ShoppingListCommand[R] extends ReplyType[R]


case class CreateList(listId: String, name: String, ownerId: String) extends ShoppingListCommand[ShoppingListId]

object CreateList {
  implicit val format: Format[CreateList] = Json.format
}


case class GetList(listId: String) extends ShoppingListCommand[ShoppingList]

object GetList {
  implicit val format: Format[GetList] = Json.format
}


case class ShareList(listId: String, sharedByUserId: String, sharedWithUserId: String) extends ShoppingListCommand[NotUsed]

object ShareList {
  implicit val format: Format[ShareList] = Json.format
}


case class AddItem(listId: String, addedByUserId: String, name: String) extends ShoppingListCommand[ShoppingItemId]

object AddItem {
  implicit val format: Format[AddItem] = Json.format
}
