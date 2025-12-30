# zio-messaging

[![CI](https://github.com/io-github-nafg/zio-messaging/actions/workflows/ci.yml/badge.svg)](https://github.com/io-github-nafg/zio-messaging/actions/workflows/ci.yml)

ZIO wrappers for messaging APIs, providing type-safe, functional interfaces for sending SMS messages through various providers.

## Features

- **Type-safe**: Uses `PhoneNumber` from [scala-phonenumber](https://github.com/nafg/scala-phonenumber) for validated phone numbers
- **Functional**: Built on ZIO for composable, testable, and error-safe code
- **Modular**: Choose the integration you need (Twilio, Entrance Group, or implement your own)
- **Cross-compiled**: Supports both Scala 2.13 and Scala 3

## Modules

### `zio-messaging` (Core)

The core module defines the `SmsService` trait that all implementations extend:

```scala
trait SmsService {
  def sendMessage(to: Seq[PhoneNumber], message: String): Task[Unit]
}
```

Also includes a no-op implementation that doesn't send messages (useful for testing, development, or disabling SMS):

```scala
SmsService.NoOp.layer
```

### `zio-messaging-twilio`

Twilio SMS integration using the official Twilio SDK.

### `zio-messaging-entrance-group`

Entrance Group SMS integration with two implementation options:
- **Hook Campaign**: Use an existing campaign via webhook
- **Manual Campaign**: Create new campaigns per message

See [zio-messaging-entrance-group/README.md](zio-messaging-entrance-group/README.md) for usage examples and details.

## Installation

Add the following to your `build.sbt`:

```scala
libraryDependencies += "io.github.nafg.zio-messaging" %% "zio-messaging-twilio" % "<version>"
```

For Bleep (`bleep.yaml`):

```yaml
dependencies:
  - io.github.nafg.zio-messaging::zio-messaging-twilio:<version>
```

## Usage

### Twilio Example

```scala
import io.github.nafg.messaging.SmsService
import io.github.nafg.messaging.twilio.{TwilioClient, TwilioSmsService}
import io.github.nafg.scalaphonenumber.PhoneNumber
import zio._

val twilioClientLayer = ZLayer.succeed(
  TwilioClient.Config(
    accountSid = "your-account-sid",
    authToken = "your-auth-token"
  )
) >>> TwilioClient.layer

val twilioSmsLayer = ZLayer.succeed(
  TwilioSmsService.Config(
    from = PhoneNumber.parseInternational("+15551234567").get
  )
) ++ twilioClientLayer >>> TwilioSmsService.layer

val program = for {
  sms <- ZIO.service[SmsService]
  recipient = PhoneNumber.parseInternational("+15559876543").get
  _ <- sms.sendMessage(Seq(recipient), "Hello from ZIO!")
} yield ()

program.provide(twilioSmsLayer)
```

## No-Op Implementation

When you don't want to send actual messages (e.g., testing, local development, or feature toggling), use the no-op implementation:

```scala
import io.github.nafg.messaging.SmsService

val program = for {
  sms <- ZIO.service[SmsService]
  _ <- sms.sendMessage(recipients, "This won't actually send")
} yield ()

program.provide(SmsService.NoOp.layer)
```

## Phone Number Handling

All phone numbers use the `PhoneNumber` type from [scala-phonenumber](https://github.com/nafg/scala-phonenumber):

```scala
import io.github.nafg.scalaphonenumber.PhoneNumber

// Parse international format
val number = PhoneNumber.parseInternational("+15551234567")

// Format as E.164
number.map(_.formatE164) // "+15551234567"
```

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.