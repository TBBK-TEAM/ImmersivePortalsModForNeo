# Changelog

All notable changes to this project will be documented in this file.  
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project tries to adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased Changes]

None currently

## [6.1.1] - 2026-09-28

### Added

- Chinese (Simplified) translations for all command feedback, portal wand messages and warnings.

### Changed

- The mod info check and the update notification are now disabled by default. They never included a UI to turn them off, so re-enabling them currently requires editing the config file.
- The first-run welcome screen is now marked as already shown by default.

### Fixed

- Player-facing messages were hardcoded English and could not be translated; they are now translation keys.
- Corrected the release dates of 6.0.8 and 6.0.9 in this changelog, and added the missing reference link for 6.1.0.

## [6.1.0] - 2026-09-28

### Added

- Every command stick variant now has its own icon (42 distinct vanilla-style textures) instead of all variants sharing a single texture.

### Changed

- Sodium compatibility updated to 0.8.13 and Iris to 1.8.14-beta.1 (Iris 1.8.14 is the first release built against Sodium 0.8).
- Development builds now use NeoForge 21.1.250. Sodium 0.8.13 bundles a Forgified Fabric API that requires NeoForge 21.1.219 or newer.

### Fixed

- Missing texture (black/purple) on the mod logo in the first-run welcome screen. The icon had been moved from `assets/immersive_portals/icon.png` to the jar root for `logoFile` without updating the screen, which still requested the old resource location.
- Dependency resolution failure caused by `maven.modrinth:stitch`, a slug that no longer resolves; the mod is published as `athena-ctm`.
- Cloth Config is now declared as a required dependency, so a missing prerequisite reports a clear error instead of a raw `NoClassDefFoundError`.
- Sodium and Iris mixins could not be applied in a development environment, because Sodium 0.8.13 ships as a jar-in-jar wrapper whose nested mod jar reuses the same mod id, which made FML's JarJar drop it.

## [6.0.9] - 2026-09-27

### Fixed

- Post-respawn disconnect caused by `ClientboundPlayerPositionPacket` missing dimension field (fixes NPE on respawn).

## [6.0.8] - 2026-09-27

### Added

- CI/CD pipeline for automated builds and GitHub releases.

## [6.0.7] - 2025-06-18

### Fixed

- Oritech animations not displaying ([#13](https://github.com/iPortalTeam/ImmersivePortalsModForNeo/issues/13)).
- ComputerCraft monitors not displaying anything ([#31](https://github.com/iPortalTeam/ImmersivePortalsModForNeo/issues/31)).

## [6.0.6] - 2024-12-22

### Updated

- Sync upstream (v6.0.6)
- Sodium compat (v0.6.0)
- Iris compat (v1.8.0) (experimental)

### Fixed

- Default config values being wrong

## [6.0.3] - 2024-10-20

### Added

- Initial port to NeoForge 1.21.1

### Known Issues

- Iris compatibility is not fully functional
- Crash with SecurityCraft

[Unreleased Changes]: https://github.com/iPortalTeam/ImmersivePortalsModForNeo/compare/v6.1.1...HEAD
[6.1.1]: https://github.com/iPortalTeam/ImmersivePortalsModForNeo/releases/tag/v6.1.1
[6.1.0]: https://github.com/iPortalTeam/ImmersivePortalsModForNeo/releases/tag/v6.1.0
[6.0.9]: https://github.com/iPortalTeam/ImmersivePortalsModForNeo/releases/tag/v6.0.9
[6.0.8]: https://github.com/iPortalTeam/ImmersivePortalsModForNeo/releases/tag/v6.0.8
[6.0.7]: https://github.com/iPortalTeam/ImmersivePortalsModForNeo/releases/tag/v6.0.7
[6.0.6]: https://github.com/iPortalTeam/ImmersivePortalsModForNeo/releases/tag/v6.0.6
[6.0.3]: https://github.com/iPortalTeam/ImmersivePortalsModForNeo/releases/tag/v6.0.3

