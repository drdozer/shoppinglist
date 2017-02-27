package uk.co.turingatemyhamster.shoppinglist.user.impl

import java.time.Instant

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger}
import play.api.libs.json.{Format, Json}

/**
  *
  *
  * @author Matthew Pocock
  */
sealed trait UserEvent extends AggregateEvent[UserEvent] {
  override def aggregateTag: AggregateEventTagger[UserEvent] = UserEvent.Tag
}

object UserEvent {
  val NumShards = 4
  val Tag = AggregateEventTag.sharded[UserEvent](NumShards)
}

case class UserCreated(userId: String, email: String, timestamp: Instant = Instant.now()) extends UserEvent

object UserCreated {
  implicit val format: Format[UserCreated] = Json.format
}


case class FriendAdded(userId: String, friendId: String, timestamp: Instant = Instant.now()) extends UserEvent

object FriendAdded {
  implicit val format: Format[FriendAdded] = Json.format
}
