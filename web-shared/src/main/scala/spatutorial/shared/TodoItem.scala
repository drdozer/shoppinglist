package spatutorial.shared

import upickle.default._

sealed trait TodoPriority

case object TodoLow extends TodoPriority

case object TodoNormal extends TodoPriority

case object TodoHigh extends TodoPriority

case class TodoItem(id: String, timeStamp: Int, content: String, priority: TodoPriority, completed: Boolean)

object TodoItem {
  implicit val readWriter: ReadWriter[TodoItem] = macroRW[TodoItem]
}

object TodoPriority {
  // note: may need macroRW[TodoLow] merge ... SI-7046
  implicit val readWriter: ReadWriter[TodoPriority] = macroRW[TodoPriority]
}
