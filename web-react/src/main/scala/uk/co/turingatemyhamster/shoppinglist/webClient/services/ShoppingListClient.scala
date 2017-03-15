package uk.co.turingatemyhamster.shoppinglist.webClient.services

import upickle.Js
import upickle.default._

import scala.collection.immutable
import scala.concurrent.Future

/**
  *
  *
  * @author Matthew Pocock
  */
class ShoppingListClient(ajaxClient: AjaxClient) {
  import ShoppingListClient._

  def createList(newList: NewList): Future[ShoppingListId] =
    ajaxClient.post[NewList, ShoppingListId]("/api/shoppinglist", newList)

  def getList(shoppingListId: String): Future[ShoppingList] =
    ajaxClient.get[ShoppingList](s"/api/shoppinglist/$shoppingListId")

  def shareList(shoppingListId: String, share: Share): Future[Unit] =
    ajaxClient.post[Share, Unit](s"/api/shoppinglist/$shoppingListId", share)

  def addItem(shoppingListId: String, newItem: NewItem): Future[ShoppingItemId] =
    ajaxClient.post[NewItem, ShoppingItemId](s"/api.shoppinglist/$shoppingListId", newItem)
}

object ShoppingListClient {
  case class NewList(ownerId: String, name: String)

  object NewList {
    implicit val format: ReadWriter[NewList] = macroRW[NewList]
  }


  case class ShoppingListId(id: String)

  object ShoppingListId {
    implicit val format: ReadWriter[ShoppingListId] = macroRW[ShoppingListId]
  }


  case class Share(sharedByUserId: String, sharedWithUserId: String)

  object Share {
    implicit val format: ReadWriter[Share] = macroRW[Share]
  }


  case class ShoppingList(id: String, name: String, items: immutable.Seq[ShoppingItem]) {
    def addItem(item: ShoppingItem): ShoppingList =
      if(items.contains(item)) this
      else this.copy(items = items :+ item)
  }

  object ShoppingList {
    implicit val format: ReadWriter[ShoppingList] = macroRW[ShoppingList]
  }


  case class NewItem(addedByUserId: String, name: String)

  object NewItem {
    implicit val format: ReadWriter[NewItem] = macroRW[NewItem]
  }


  case class ShoppingItemId(id: String)

  object ShoppingItemId {
    implicit val format: ReadWriter[ShoppingItemId] = macroRW[ShoppingItemId]
  }


  case class ShoppingItem(id: String, addedByUserId: String, name: String, times: Option[Int])

  object ShoppingItem {
    implicit val format: ReadWriter[ShoppingItem] = macroRW[ShoppingItem]
  }


  case class UserLists(has: immutable.Seq[UserHasList])

  object UserLists {
    implicit val format: ReadWriter[UserLists] = macroRW[UserLists]
  }


  case class UserHasList(userId: String, role: UserRole, shoppingListId: String)

  object UserHasList {
    implicit val format: ReadWriter[UserHasList] = macroRW[UserHasList]
  }


  sealed trait UserRole

  object UserRole {
    case object Owner extends UserRole
    case object SharedWith extends UserRole

    implicit object format extends ReadWriter[UserRole] {
      override def write0: (UserRole) => Js.Value = {
        case Owner => Js.Str("owner")
        case SharedWith => Js.Str("sharedWith")
      }

      override def read0: PartialFunction[Js.Value, UserRole] = {
        case Js.Str("owner") => Owner
        case Js.Str("sharedWith") => SharedWith
      }
    }
  }
}