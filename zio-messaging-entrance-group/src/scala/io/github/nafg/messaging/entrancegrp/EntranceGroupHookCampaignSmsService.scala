package io.github.nafg.messaging.entrancegrp

import io.github.nafg.messaging.SmsService
import io.github.nafg.scalaphonenumber.PhoneNumber

import zio.http.Client
import zio.http.endpoint.EndpointExecutor
import zio.{Task, ZIO, ZLayer}

class EntranceGroupHookCampaignSmsService(client: Client, config: EntranceGroupHookCampaignSmsService.Config)
    extends SmsService                     {
  private val endpointExecutor =
    EndpointExecutor(
      client.addHeader(config.authKey.stringValue, config.authValue.stringValue),
      EntranceGroupApi.com.entrancegrp.`/api`.baseUrl
    )

  def sendMessage(
    to: PhoneNumber,
    message: String
  ): ZIO[Any, Nothing, EntranceGroupApi.com.entrancegrp.`/api`.hooks.campaigns.ResponseBody] =
    ZIO.scoped {
      endpointExecutor(
        EntranceGroupApi.com.entrancegrp.`/api`.hooks.campaigns.endpoint(
          (config.campaignId, EntranceGroupApi.com.entrancegrp.`/api`.hooks.campaigns.RequestBody(to, message))
        )
      )
        .filterOrDieWith(_.code == 200) { body =>
          new RuntimeException(s"Sending SMS failed with ${body.code}")
        }
    }

  override def sendMessage(to: Seq[PhoneNumber], message: String): Task[Unit] =
    ZIO.foreachDiscard(to)(sendMessage(_, message))
}
object EntranceGroupHookCampaignSmsService {
  case class Config(campaignId: Long, authKey: zio.Config.Secret, authValue: zio.Config.Secret)

  val layer: ZLayer[Client & Config, Nothing, EntranceGroupHookCampaignSmsService] =
    ZLayer.fromFunction(new EntranceGroupHookCampaignSmsService(_, _))
}
