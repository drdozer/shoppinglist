package uk.co.turingatemyhamster.shoppinglist.shoppinglist.impl

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import uk.co.turingatemyhamster.shoppinglist.shoppinglist.api._

/**
  *
  *
  * @author Matthew Pocock
  */
class ShoppingListServiceImpl(persistenceEntityRegistration: PersistentEntityRegistry) extends ShoppingListService {

  override def createList: ServiceCall[NewList, ShoppingListId] = ServiceCall { newList =>
    val slId = UUID.randomUUID().toString
    val ref = persistenceEntityRegistration.refFor[ShoppingListEntity](slId)

    ref.ask(CreateList(listId = slId, name = newList.name, ownerId = newList.ownerId))
  }

  override def getList(shoppingListId: String): ServiceCall[NotUsed, ShoppingList] = ServiceCall { _ =>
    val ref = persistenceEntityRegistration.refFor[ShoppingListEntity](shoppingListId)

    ref.ask(GetList(listId = shoppingListId))
  }

  override def shareList(shoppingListId: String): ServiceCall[Share, NotUsed] = ServiceCall { share =>
    val ref = persistenceEntityRegistration.refFor[ShoppingListEntity](shoppingListId)

    ref.ask(ShareList(
      listId = shoppingListId,
      sharedByUserId = share.sharedByUserId,
      sharedWithUserId = share.sharedWithUserId))
  }

  override def addItem(shoppingListId: String): ServiceCall[NewItem, ShoppingItemId] = ServiceCall { newItem =>
    val ref = persistenceEntityRegistration.refFor[ShoppingListEntity](shoppingListId)

    ref.ask(AddItem(listId = shoppingListId, addedByUserId = newItem.addedByUserId, name = newItem.name))
  }
}
