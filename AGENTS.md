# Repository Guidelines

## Project Structure & Module Organization
- `bleep.yaml` defines a multi-module build with shared templates and cross-builds (Scala 2.13/3).
- Modules live at the repo root: `zio-messaging` (core API), `zio-messaging-twilio` (Twilio integration), and `zio-messaging-entrance-group` (Entrance Group integration).
- Source code follows the bleep “cross-pure” layout under `*/src/scala/...` (for example, `zio-messaging/src/scala/io/github/nafg/messaging`).
- There are currently no test sources; if you add tests, place them under `*/src/test/scala/...` for the relevant module.

## Build, Test, and Development Commands
- `bleep projects` — list available projects/modules.
- `bleep compile <project>` — compile a module, e.g. `bleep compile zio-messaging`.
- `bleep test <project>` — run tests for a module (no tests yet, but this is the standard entry point once added).
- These modules are libraries (no runnable app entry point in this repo), so there is no default `run` target.

## Coding Style & Naming Conventions
- Formatting is enforced by Scalafmt (`.scalafmt.conf`): IntelliJ preset, `maxColumn = 120`, `scala213source3` dialect.
- Prefer letting Scalafmt handle indentation/alignment instead of manual formatting.
- Follow existing package structure (`io.github.nafg.messaging.*`), PascalCase for types, camelCase for methods/vals.

## Testing Guidelines
- If adding tests, add the test framework dependency in `bleep.yaml` and place tests in the module’s `src/test/scala` tree.
- Name specs descriptively (e.g., `TwilioSmsServiceSpec`) and keep tests close to the corresponding module.

## Commit & Pull Request Guidelines
- Commit messages follow Conventional Commits with optional scope, e.g. `refactor(api): ...`, `chore(deps): ...`, `ci: ...`.
- Prefer small, focused PRs with: a clear summary, testing notes (or “not applicable”), and linked issues when relevant.
- If you update public APIs, also update documentation or usage examples where applicable.

## Release & Publishing Notes
- Publishing metadata lives in `bleep.publish.yaml` and uses the `scripts` project configured in `bleep.yaml`.
- Tags follow a `vX.Y.Z` pattern (for example, `v0.3.0`), so align release notes and versioning accordingly.
