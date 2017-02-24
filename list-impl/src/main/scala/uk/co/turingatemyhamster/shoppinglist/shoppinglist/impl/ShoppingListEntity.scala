package uk.co.turingatemyhamster.shoppinglist.shoppinglist.impl

import java.time.Instant
import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import play.api.libs.json.{Format, Json}
import uk.co.turingatemyhamster.shoppinglist.shoppinglist.api._

import scala.collection.immutable

class ShoppingListEntity extends PersistentEntity {

  override type Command = ShoppingListCommand[_]
  override type Event = ShoppingListEvent
  override type State = ShoppingListState

  override def initialState: ShoppingListState = ShoppingListState(None)

  override def behavior: Behavior = {
    case ShoppingListState(maybeAList, _) => Actions().onCommand[CreateList, ShoppingListId] {
      case (CreateList(listId, name, ownerId), ctx, state) =>
        // todo: sould validate that state is ShoppingListState(None, _)
        ctx.thenPersist(ListCreated(listId, name, ownerId)) { _ =>
          ctx.reply(ShoppingListId(listId))
        }
    }.onCommand[ShareList, NotUsed] {
      case (ShareList(listId, sharedByUserId, sharedWithUserId), ctx, state) =>
        ctx.thenPersist(ListShared(listId, sharedByUserId, sharedWithUserId)) { _ =>
          ctx.reply(NotUsed)
        }
    }.onCommand[AddItem, ShoppingItemId] {
      case (AddItem(listId, addedByUserId, name), ctx, state) =>
        val itemId = UUID.randomUUID().toString
        ctx.thenPersist(ItemAdded(listId, itemId, addedByUserId, name)) { _ =>
          ctx.reply(ShoppingItemId(itemId))
        }
    }.onReadOnlyCommand[GetList, ShoppingList] {
      case (GetList(listId), ctx, ShoppingListState(None, _)) =>
        ctx.invalidCommand(s"No shopping list for id: $listId")
      case (GetList(listId), ctx, ShoppingListState(Some(las), _)) =>
        ctx.reply(las.shoppingList)
    }.onEvent {
      case (ListCreated(listId, name, ownerId, _), ShoppingListState(None, _)) =>
        ShoppingListState(Some(ShoppingListAndShares(
          ShoppingList(listId, name, immutable.Seq.empty),
          immutable.Seq.empty
        )))
      case (ListShared(listId, sharedByUserId, sharedWithUserId, _), ShoppingListState(Some(listAndShares), _)) =>
        ShoppingListState(Some(ShoppingListAndShares(
          listAndShares.shoppingList,
          listAndShares.sharedWith :+ Share(sharedByUserId, sharedWithUserId)
        )))
      case (ItemAdded(listId, itemId, addeByUserId, name: String), ShoppingListState(Some(listAndShares), _)) =>
        ShoppingListState(Some(ShoppingListAndShares(
          listAndShares.shoppingList.addItem(ShoppingItem(itemId, addeByUserId, name, None)),
          listAndShares.sharedWith
        )))
    }
  }

}

case class ShoppingListState(aList: Option[ShoppingListAndShares], timestamp: Instant = Instant.now())

object ShoppingListState {
  implicit val format: Format[ShoppingListState] = Json.format
}


case class ShoppingListAndShares(shoppingList: ShoppingList, sharedWith: immutable.Seq[Share])

object ShoppingListAndShares {
  implicit val format: Format[ShoppingListAndShares] = Json.format
}