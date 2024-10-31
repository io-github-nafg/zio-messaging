package io.github.nafg.messaging.twilio


import com.twilio.http.TwilioRestClient
import zio.{Config, ZLayer}

case class TwilioClient(twilioRestClient: TwilioRestClient)
object TwilioClient {
  case class Config(accountSid: String, authToken: String)
  
  val layer = ZLayer.fromFunction { (config: Config) =>
    TwilioClient(
      new TwilioRestClient.Builder(config.accountSid, config.authToken)
        .build()
    )
  }
}
