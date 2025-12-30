# zio-messaging-entrance-group

Entrance Group SMS integration for ZIO applications.

## Overview

This module provides two implementations for sending SMS messages through Entrance Group:

1. **Hook Campaign** (`EntranceGroupHookCampaignSmsService`): Uses an existing campaign via webhook
2. **Manual Campaign** (`EntranceGroupCreateManualCampaignSmsService`): Creates new campaigns per message

## Installation

Add to your `build.sbt`:

```scala
libraryDependencies += "io.github.nafg.zio-messaging" %% "zio-messaging-entrance-group" % "<version>"
```

For Bleep (`bleep.yaml`):

```yaml
dependencies:
  - io.github.nafg.zio-messaging::zio-messaging-entrance-group:<version>
```

## Usage

### Hook Campaign Example

Use this when you have an existing campaign and want to send messages via its webhook:

```scala
import io.github.nafg.messaging.SmsService
import io.github.nafg.messaging.entrancegrp.EntranceGroupHookCampaignSmsService
import io.github.nafg.scalaphonenumber.PhoneNumber
import zio._
import zio.http.Client

val entranceGroupLayer = ZLayer.succeed(
  EntranceGroupHookCampaignSmsService.Config(
    campaignId = 12345L,
    authKey = Config.Secret("X-API-Key"),
    authValue = Config.Secret("your-api-key")
  )
) ++ Client.default >>> EntranceGroupHookCampaignSmsService.layer

val program = for {
  sms <- ZIO.service[SmsService]
  recipient = PhoneNumber.parseInternational("+15559876543").get
  _ <- sms.sendMessage(Seq(recipient), "Hello from Entrance Group!")
} yield ()

program.provide(entranceGroupLayer)
```

### Manual Campaign Example

Use this when you want to create a new campaign for each batch of messages:

```scala
import io.github.nafg.messaging.SmsService
import io.github.nafg.messaging.entrancegrp.EntranceGroupCreateManualCampaignSmsService
import io.github.nafg.scalaphonenumber.PhoneNumber
import zio._
import zio.http.Client

val entranceGroupLayer = ZLayer.succeed(
  EntranceGroupCreateManualCampaignSmsService.Config(
    campaignName = "My Campaign",
    authKey = Config.Secret("X-API-Key"),
    authValue = Config.Secret("your-api-key")
  )
) ++ Client.default >>> EntranceGroupCreateManualCampaignSmsService.layer

val program = for {
  sms <- ZIO.service[SmsService]
  recipients = Seq(
    PhoneNumber.parseInternational("+15551111111").get,
    PhoneNumber.parseInternational("+15552222222").get
  )
  _ <- sms.sendMessage(recipients, "Broadcast message!")
} yield ()

program.provide(entranceGroupLayer)
```

## Configuration

### EntranceGroupHookCampaignSmsService.Config

- `campaignId: Long` - The ID of the existing campaign
- `authKey: Config.Secret` - The authentication header key (e.g., "X-API-Key")
- `authValue: Config.Secret` - The authentication header value (your API key)

### EntranceGroupCreateManualCampaignSmsService.Config

- `campaignName: String` - The name for the campaigns that will be created
- `authKey: Config.Secret` - The authentication header key (e.g., "X-API-Key")
- `authValue: Config.Secret` - The authentication header value (your API key)

## API Endpoints

This module uses two Entrance Group API endpoints:

- **Hook Campaign**: `POST https://entrancegrp.com/api/hooks/campaigns/{id}`
- **Manual Campaign**: `POST https://apiv2.entrancegrp.com/campaigns`

All endpoints are defined in `EntranceGroupApi` using zio-http's type-safe endpoint DSL.

## Dependencies

- `zio-messaging` (core module)
- `zio-http` for HTTP client and endpoint definitions
- `scala-phonenumber` for phone number handling

## License

Licensed under the Apache License, Version 2.0. See the main [LICENSE](../LICENSE) file for details.