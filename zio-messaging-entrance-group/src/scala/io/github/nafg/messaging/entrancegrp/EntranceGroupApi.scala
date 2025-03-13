package io.github.nafg.messaging.entrancegrp

import io.github.nafg.scalaphonenumber.PhoneNumber

import zio.http.*
import zio.http.Method.POST
import zio.http.endpoint.{Endpoint, EndpointLocator}
import zio.schema.{DeriveSchema, Schema}

object EntranceGroupApi {
  implicit val schemaPhoneNumber: Schema[PhoneNumber] =
    Schema[String].transformOrFail(
      PhoneNumber.parseInternational(_).toEither.left.map(_.toString),
      n => Right(n.formatE164)
    )

  object com {
    object entrancegrp {
      object `/api` {
        object hooks {
          object campaigns {
            case class RequestBody(number: PhoneNumber, message: String)
            object RequestBody {
              implicit val schemaRequestBody: Schema[RequestBody] = DeriveSchema.gen[RequestBody]
            }

            case class ResponseBody(code: Int)
            object ResponseBody {
              implicit val schemaResponseBody: Schema[ResponseBody] = DeriveSchema.gen[ResponseBody]
            }

            val endpoint =
              Endpoint(POST / "hooks" / "campaigns" / long("id"))
                .in[RequestBody]
                .out[ResponseBody]
          }
        }

        val endpointLocator =
          EndpointLocator.fromURL(URL(Path.root / "api", URL.Location.Absolute(Scheme.HTTPS, "entrancegrp.com", None)))
      }

      object apiv2 {
        object campaigns {
          object create {
            case class RequestBody(name: String, `type`: String, numbers: List[PhoneNumber], text_message: String)
            object RequestBody  {
              implicit val schemaRequestBody: Schema[RequestBody] = DeriveSchema.gen[RequestBody]
            }
            case class ResponseBody(code: Int, message: String)
            object ResponseBody {
              implicit val schemaResponseBody: Schema[ResponseBody] = DeriveSchema.gen[ResponseBody]
            }

            val endpoint =
              Endpoint(POST / "campaigns")
                .in[RequestBody]
                .out[ResponseBody]
          }
        }
      }

      val endpointLocator =
        EndpointLocator.fromURL(URL(Path.root, URL.Location.Absolute(Scheme.HTTPS, "apiv2.entrancegrp.com", None)))
    }
  }
}
