# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build System

This project uses **Bleep** as its build tool.

### Common Commands

- **List all projects**: `bleep projects`
- **Compile all modules**: `bleep compile`
- **Compile specific module**: `bleep compile <project-name>` (e.g., `bleep compile zio-messaging`)
- **Run tests**: `bleep test <project-name>` (once tests are added)
- **Publish**: `bleep publish -- --mode=portal-api:AUTOMATIC` (requires credentials in env vars)

### Important Build Notes

- The build uses `source-layout: cross-pure` with sources under `*/src/scala/...`
- All modules cross-build for Scala 2.13 and Scala 3 using the `template-cross-all` template
- JVM target: GraalVM Java 17 (22.3.1)

## Project Architecture

This is a **multi-module library** for ZIO-based messaging integrations:

### Module Structure

1. **zio-messaging** (core module)
   - Defines the `SmsService` trait with `sendMessage(to: Seq[PhoneNumber], message: String): Task[Unit]`
   - Provides `SmsService.NoOp` as a no-op implementation
   - Uses `io.github.nafg.scalaphonenumber` for phone number handling
   - No external messaging dependencies

2. **zio-messaging-twilio**
   - Depends on: `zio-messaging`, Twilio SDK
   - Implements `TwilioSmsService` that wraps Twilio's REST API
   - Requires `TwilioClient` and `TwilioSmsService.Config(from: PhoneNumber)`
   - Provides ZLayer for dependency injection

3. **zio-messaging-entrance-group**
   - Depends on: `zio-messaging`, zio-http
   - Two implementations:
     - `EntranceGroupHookCampaignSmsService`: uses existing campaign via webhook
     - `EntranceGroupCreateManualCampaignSmsService`: creates new campaigns per message
   - `EntranceGroupApi` defines typed endpoints using zio-http's endpoint DSL
   - Uses Schema-based serialization for request/response bodies

### Design Patterns

- All implementations extend the common `SmsService` trait
- ZLayer-based dependency injection (e.g., `TwilioSmsService.layer`, `EntranceGroupHookCampaignSmsService.layer`)
- Config case classes for service configuration (e.g., `Config(from: PhoneNumber)`, `Config(campaignId: Long, authKey: Secret, authValue: Secret)`)
- Phone numbers typed using `PhoneNumber` from scala-phonenumber library (E.164 format)
- ZIO effects for error handling and async operations

## Code Formatting

- Scalafmt is configured (`.scalafmt.conf`)
- Uses IntelliJ preset with `maxColumn = 120`
- Dialect: `scala213source3` for Scala 2.13 with `-Xsource:3` compatibility
- Always format code before committing

## Package Structure

- Base package: `io.github.nafg.messaging`
- Twilio: `io.github.nafg.messaging.twilio`
- Entrance Group: `io.github.nafg.messaging.entrancegrp`

## Publishing

- Publishing metadata in `bleep.publish.yaml`
- Group ID: `io.github.nafg.zio-messaging`
- All three modules are published to Sonatype
- Tags follow `vX.Y.Z` pattern (e.g., `v0.3.0`)

## CI/CD

- GitHub Actions workflow in `.github/workflows/ci.yml`
- Runs on: push to main, PRs, tag pushes, merge groups
- Build job: compiles all modules with `bleep compile`
- Publish job: automatically publishes on version tags (`v*`) to Sonatype using portal API