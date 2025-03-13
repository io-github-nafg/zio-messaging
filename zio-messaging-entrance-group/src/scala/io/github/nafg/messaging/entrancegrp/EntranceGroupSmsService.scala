package io.github.nafg.messaging.entrancegrp

import io.github.nafg.messaging.SmsService
import io.github.nafg.scalaphonenumber.PhoneNumber

import zio.http.Method.POST
import zio.http.endpoint.{Endpoint, EndpointExecutor, EndpointLocator}
import zio.http.{Client, Path, Scheme, URL, long}
import zio.schema.{DeriveSchema, Schema}
import zio.{Scope, Task, ZIO, ZLayer}

class EntranceGroupSmsService(client: Client, config: EntranceGroupSmsService.Config) extends SmsService {
  private val endpointExecutor: EndpointExecutor[Any, Unit, Scope]       =
    EndpointExecutor(client.addHeader(config.authKey, config.authValue), EntranceGroupSmsService.endpointLocator)
  override def sendMessage(to: PhoneNumber, message: String): Task[Unit] =
    ZIO.scoped {
      endpointExecutor(
        EntranceGroupSmsService.endpoint((config.campaignId, EntranceGroupSmsService.RequestBody(to, message)))
      )
        .filterOrDieWith(_.code == 200) { body =>
          new RuntimeException(s"Sending SMS failed with ${body.code}")
        }
        .unit
    }
}
object EntranceGroupSmsService {
  case class Config(campaignId: Long, authKey: String, authValue: String)

  case class RequestBody(number: PhoneNumber, message: String)
  private object RequestBody {
    implicit val schemaPhoneNumber: Schema[PhoneNumber] =
      Schema[String].transformOrFail(
        PhoneNumber.parseInternational(_).toEither.left.map(_.toString),
        n => Right(n.formatE164)
      )
    implicit val codec: Schema[RequestBody]             = DeriveSchema.gen[RequestBody]
  }

  case class ResponseBody(code: Int)
  object ResponseBody {
    implicit val schemaResponseBody: Schema[ResponseBody] = DeriveSchema.gen[ResponseBody]
  }

  private val endpoint =
    Endpoint(POST / "hooks" / "campaigns" / long("id"))
      .in[RequestBody]
      .out[ResponseBody]

  private val endpointLocator =
    EndpointLocator.fromURL(URL(Path.root / "api", URL.Location.Absolute(Scheme.HTTPS, "entrancegrp.com", None)))

  val layer: ZLayer[Client & Config, Nothing, EntranceGroupSmsService] =
    ZLayer.fromFunction(new EntranceGroupSmsService(_, _))
}
