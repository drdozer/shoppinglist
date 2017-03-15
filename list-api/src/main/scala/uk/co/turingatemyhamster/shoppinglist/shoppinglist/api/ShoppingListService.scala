package uk.co.turingatemyhamster.shoppinglist.shoppinglist.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api._
import play.api.libs.json._

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

  def listsForUser(userId: String): ServiceCall[NotUsed, UserLists]

  override final def descriptor = {
    import Service._

    named("shoppinglist").withCalls(
      pathCall("/api/shoppinglist", createList),
      pathCall("/api/shoppinglist/:id", getList _),
      pathCall("/api/shoppinglist/:id/sharedWith", shareList _),
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


case class UserLists(has: immutable.Seq[UserHasList])

object UserLists {
  implicit val format: Format[UserLists] = Json.format
}


case class UserHasList(userId: String, role: UserRole, shoppingListId: String)

object UserHasList {
  implicit val format: Format[UserHasList] = Json.format
}


sealed trait UserRole

object UserRole {
  case object Owner extends UserRole
  case object SharedWith extends UserRole

  implicit object reads extends Format[UserRole] {
    override def reads(json: JsValue): JsResult[UserRole] = json match {
      case JsString("owner") => JsSuccess(Owner)
      case JsString("sharedWith") => JsSuccess(SharedWith)
      case JsString(f) => JsError(s"The value $f is not a user role")
      case _ => JsError("Expecting a string")
    }

    override def writes(o: UserRole): JsValue = o match {
      case Owner => Json.toJson("owner")
      case SharedWith => Json.toJson("sharedWith")
    }
  }
}