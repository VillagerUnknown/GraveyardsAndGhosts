# Changelog

All notable changes to this project will be documented in this file.

## [1.3.0+1.21.1]

This update resolves stuttering issues and improves existing features.

_This update adds a new option that will affect users disabling player ghosts on death._

### Added

- Added `enableGraveyardRespawnPoints` option. This option is set to true by default. 
_This option allows players to allow respawns at graveyards without the Ghost Effect. 
The Ghost Effect will only be applied if both `enableGraveyardRespawnPoints` and `enablePlayerGhostOnDeath` are set to true.
If you had `enablePlayerGhostOnDeath` set to false, you will want to set `enableGraveyardRespawnPoints` to false for the same configuration._
- Added `enablePlayerGhostParticles` option. This option is set to true by default.

### Changed

- Changed supported Platform version to 1.3.0. _This resolves stuttering issues and includes new features._
- Changed mixin filenames to avoid collisions.
- Changed usage of `TickTimer` to `ServerTickTimer`. _This helps resolve stuttering issues._
- Changed most blocks to act like their respective material types. 
_Resurrection statues are now only blast resistant if made from Obsidian, Crying Obsidian, or Netherite. 
The tombstones for these same block types no longer instant break to represent their hardness._

### Fixed

- Fixed breaking times of Resurrection Statues. _Resurrection statues were copying Netherite Block properties and had an extremely long breaking time._
- Fixed location of language files.

## [1.2.0]

### Added

- Added additional variations of generated Small Graveyards.
- Novice Cartographers now have a chance of selling maps to Graveyards.

### Changed

- Players with the Ghost effect no longer need an empty hand to interact with Resurrection Statues.
- Changed Groundskeeper sales prices.

### Removed

- Removed generated Large and Medium Graveyards.

## [1.1.0]

### Added

- Added Groundskeeper villager with Graveyard block trades.
- Added Concrete block variations.
- Added Terracotta block variations.
- Added Terracotta graveyards to Badlands biomes.

### Changed

- Simplified the recipe for Resurrection Statues.
- Villagers and Iron Golems no longer have a chance to spawn entities when stepping on Grave Soil.

### Fixed

- Two block tall statues can no longer be duplicated by breaking the top half first.
- Coffins can no longer be duplicated by breaking them.

## [1.0.1]

### Added

- Added `#graveyard` tag for graveyards to make the `locate` command more useful.
- Added `#graveyard` tag for blocks and items to find items easier.

### Changed

- Changed default value for `playersStartWorldAsGhosts` option to `false` to make the mod friendlier for new players.
- Items in hand no longer display when the Ghost effect is applied. 

### Fixed

- Coffins no longer generate loot twice during world generation.
- Ghosts ignoring collision no longer get pushed out of blocks.

## [1.0.0]

_Initial release for Minecraft 1.21.1_