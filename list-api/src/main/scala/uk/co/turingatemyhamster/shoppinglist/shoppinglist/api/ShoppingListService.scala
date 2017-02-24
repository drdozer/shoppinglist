package uk.co.turingatemyhamster.shoppinglist.shoppinglist.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api._
import play.api.libs.json.{Format, Json}

import scala.collection.immutable

/**
  *
  *
  * @author Matthew Pocock
  */
trait ShoppingListService extends Service {

  def createList: ServiceCall[NewList, ShoppingListId]

  def getList(shoppingListId: String): ServiceCall[NotUsed, ShoppingList]

  def shareList(shoppingListId: String): ServiceCall[Share, NotUsed]

  def addItem(shoppingListId: String): ServiceCall[NewItem, ShoppingItemId]

  override final def descriptor = {
    import Service._

    named("shoppinglist").withCalls(
      pathCall("/api/shoppinglist", createList),
      pathCall("/api/shoppinglist/:id", getList _),
      pathCall("/api/shoppinglist/:id", shareList _),
      pathCall("/api/shoppinglist/:id/items", addItem _)
    ).withAutoAcl(true)
  }
}


case class NewList(ownerId: String, name: String)

object NewList {
  implicit val format: Format[NewList] = Json.format
}


case class ShoppingListId(id: String)

object ShoppingListId {
  implicit val format: Format[ShoppingListId] = Json.format
}


case class Share(sharedByUserId: String, sharedWithUserId: String)

object Share {
  implicit val format: Format[Share] = Json.format
}


case class ShoppingList(id: String, name: String, items: immutable.Seq[ShoppingItem]) {
  def addItem(item: ShoppingItem): ShoppingList =
    if(items.contains(item)) this
    else this.copy(items = items :+ item)
}

object ShoppingList {
  implicit val format: Format[ShoppingList] = Json.format
}


case class NewItem(addedByUserId: String, name: String)

object NewItem {
  implicit val format: Format[NewItem] = Json.format
}


case class ShoppingItemId(id: String)

object ShoppingItemId {
  implicit val format: Format[ShoppingItemId] = Json.format
}


case class ShoppingItem(id: String, addedByUserId: String, name: String, times: Option[Int])

object ShoppingItem {
  implicit val format: Format[ShoppingItem] = Json.format
}