package io.github.nafg.messaging

import io.github.nafg.scalaphonenumber.PhoneNumber

import zio.{Task, ULayer, ZIO, ZLayer}

trait SmsService  {
  def sendMessage(to: PhoneNumber, message: String): Task[Unit]
}
object SmsService {
  object NoOp extends SmsService {
    override def sendMessage(to: PhoneNumber, message: String): Task[Unit] = ZIO.unit

    val layer: ULayer[SmsService] = ZLayer.succeed(NoOp)
  }
}
