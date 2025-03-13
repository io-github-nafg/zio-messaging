package io.github.nafg.messaging.twilio

import io.github.nafg.messaging.SmsService
import io.github.nafg.scalaphonenumber.PhoneNumber

import com.twilio.`type`.PhoneNumber as TwilioPhoneNumber
import com.twilio.rest.api.v2010.account.Message
import zio.{Task, ZIO, ZLayer}

class TwilioSmsService(config: TwilioSmsService.Config, twilioClient: TwilioClient) extends SmsService {
  def sendMessage(to: PhoneNumber, message: String): Task[Message] =
    ZIO
      .fromFutureJava(
        Message
          .creator(new TwilioPhoneNumber(to.formatE164), new TwilioPhoneNumber(config.from.formatE164), message)
          .createAsync(twilioClient.twilioRestClient)
      )

  override def sendMessage(to: Seq[PhoneNumber], message: String): Task[Unit] =
    ZIO.foreachDiscard(to)(sendMessage(_, message))
}
object TwilioSmsService {
  case class Config(from: PhoneNumber)
  val layer = ZLayer.fromFunction(new TwilioSmsService(_, _))
}
