package uk.co.turingatemyhamster.shoppinglinst.webClient.services

import org.scalajs.dom.ext.Ajax
import upickle.default._

import scala.concurrent.{ExecutionContext, Future}

trait AjaxClient {
  def post[Request : Writer, Response : Reader](path: String, req: Request): Future[Response]
  def get[Response : Reader](path: String): Future[Response]
}

object AjaxClient {
  def atBaseUrl(baseUrl: String)(implicit executionContext: ExecutionContext): AjaxClient = new AjaxClient {
    override def post[Request : Writer, Response : Reader](path: String, req: Request): Future[Response] =
      Ajax.post(
        url = baseUrl + path,
        data = write(req),
        headers = Map(
          "Content-type" -> "application/json"
        ),
        responseType = "json"
      ).map { res =>
        read[Response](res.responseText)
      }

    override def get[Response : Reader](path: String): Future[Response] =
      Ajax.get(
        url = baseUrl + path,
        headers = Map(
          "Content-type" -> "application/json"
        ),
        responseType = "json"
      ).map { res =>
        read[Response](res.responseText)
      }
  }
}
