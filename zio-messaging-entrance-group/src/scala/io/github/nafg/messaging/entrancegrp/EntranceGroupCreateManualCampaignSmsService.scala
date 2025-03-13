package io.github.nafg.messaging.entrancegrp

import io.github.nafg.messaging.SmsService
import io.github.nafg.scalaphonenumber.PhoneNumber

import zio.http.Client
import zio.http.endpoint.EndpointExecutor
import zio.{Task, ZIO, ZLayer}

class EntranceGroupCreateManualCampaignSmsService(
  client: Client,
  config: EntranceGroupCreateManualCampaignSmsService.Config
) extends SmsService {
  private val endpointExecutor =
    EndpointExecutor(
      client.addHeader(config.authKey.stringValue, config.authValue.stringValue),
      EntranceGroupApi.com.entrancegrp.apiv2.endpointLocator
    )

  override def sendMessage(to: Seq[PhoneNumber], message: String): Task[Unit] =
    ZIO.scoped {
      endpointExecutor(
        EntranceGroupApi.com.entrancegrp.apiv2.campaigns.create.endpoint(
          EntranceGroupApi.com.entrancegrp.apiv2.campaigns.create
            .RequestBody(name = config.campaignName, `type` = "MANUAL", numbers = to.toList, text_message = message)
        )
      )
        .filterOrDieWith(_.code == 200) { body =>
          new RuntimeException(s"Sending SMS failed with ${body.code}")
        }
        .unit
    }
}
object EntranceGroupCreateManualCampaignSmsService {
  case class Config(campaignName: String, authKey: zio.Config.Secret, authValue: zio.Config.Secret)

  val layer: ZLayer[Client & Config, Nothing, EntranceGroupCreateManualCampaignSmsService] =
    ZLayer.fromFunction(new EntranceGroupCreateManualCampaignSmsService(_, _))
}
