# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

ALPG (mod id `alpgmc`) is a Minecraft Forge RPG mod inspired by Albion Online and Path of Exile. It targets Minecraft 1.20.1, Forge 47.2.0, Java 17, and official Mojang mappings.

The README describes the intended game (skill gems, parties, combos, dungeons). Most of that is not built yet: the code currently has one custom mob, a mob XP table with a HUD notification, and item category enums.

## Commands

Use `.\gradlew.bat` in PowerShell or `./gradlew` in Bash.

```
.\gradlew.bat build              # compile and produce the reobfuscated jar in build/libs/
.\gradlew.bat compileJava        # fastest check that the code compiles
.\gradlew.bat runClient          # launch the dev client (working dir: run/)
.\gradlew.bat runServer          # launch a dev server with --nogui
.\gradlew.bat runData            # run data generators, output to src/generated/resources/
.\gradlew.bat genIntellijRuns    # regenerate IntelliJ run configurations
```

There are no unit tests, no `src/test`, no registered gametests, and no linter. `runGameTestServer` crashes when no gametests are registered, so verify changes with `compileJava` and `runClient`.

## Architecture

Code lives in `src/main/java/com/sxxxp/ALPGMC/`:

- `ExampleMod` is the `@Mod` entry point, still largely the Forge MDK template. It wires deferred registers and common setup.
- `core/init` holds registration, `core/data` holds datapack-driven data, and `core/network` holds packets.
- `common` holds code that runs on both sides: entities and Forge bus event handlers.
- `client` holds client-only code: renderers and HUD overlays. Keep client classes here and subscribe them with `value = Dist.CLIENT`.

### Event buses

Most wiring is done by `@Mod.EventBusSubscriber` on a class, so nothing in `ExampleMod` references it. Choosing the right bus matters:

- `Bus.MOD` for lifecycle and registration events (`EntityAttributeCreationEvent`, `EntityRenderersEvent`, `RegisterGuiOverlaysEvent`).
- `Bus.FORGE` for in-game events (`LivingDeathEvent`, `AddReloadListenerEvent`).

### Mob kill XP flow

1. `common/event/DataRegisterEvent` registers `MobXpManager` as a reload listener.
2. `MobXpManager` reads `data/alpgmc/xp_tables/mob_xp.json`, which maps entity id to XP. Unlisted mobs give 10 XP.
3. `common/event/ExperienceEvent` looks up the XP on `LivingDeathEvent` when a player made the kill, then sends `XpToastPacket` to that player.
4. `client/XpUiManager` draws the notification above the hotbar.

This flow only displays the XP. No XP is stored or granted to the player yet.

### Player classes

`common/playerclass/PlayerClassData` stores the class in the player's persistent NBT and copies it on `PlayerEvent.Clone`. The server syncs it with `ClassSyncPacket`. Until a class is chosen, `client/ClassClientEvent` keeps reopening `ClassSelectScreen` and `PlayerClassEvent` cancels damage to the player. Players choose once; only the operator command `/class set <player> <class>` can change a class afterwards.

Class stats are permanent attribute modifiers in `ClassAttributes`, reapplied on login, clone, and `/class set`. Permanent matters: a transient max health modifier would be missing while the player's saved health loads, which would cut a tank's health on every relog. Values live in `ClassConfig`, a SERVER config saved per world at `serverconfig/alpgmc-server.toml`.

Restrictions live in `common/event/ClassRestrictionEvent`. Crafting limits and the warrior's arrow multiplier use `ClassResultSlot`, which wraps slot 0 of crafting menus on the server. `ClassArrowItem` is deliberately left out of the `minecraft:arrows` tag so vanilla bows can't fire it, which is why `ClassResultSlot` checks for it separately.

Prefer Forge events. Mixins in `mixin/` (config: `alpgmc.mixins.json`) are only for what events can't reach: food-only healing in `FoodDataMixin`. `PlayerClassData.get` works on both sides, but the client only knows the local player's class, so don't call it for other players on the client.

### Levels and stats

`PlayerLevelData` stores a mod-only level, XP, and stat points in persistent NBT, separate from vanilla XP. `ExperienceEvent` feeds it from the XP table, counting kills by tamed pets as the owner's. `PlayerStat` defines the stats; `ClassAttributes` applies them, except ranged damage, which `ClassCombatEvent` multiplies directly. The stat screen (`client/StatScreen`, default key K) sends `StatActionPacket`, and the server re-validates every request. Operators can use `/class addxp <player> <amount>`.

### Branches and production

`ClassProgression` answers what a player has unlocked, and is server-only because it reads the level. Tier 0 is available from level 1 and tiers 1-4 use the levels in the `progression` config. Each class has two combat branches (`PlayerBranch`), chosen at tier 1 in `client/BranchScreen`; changing later costs vanilla XP levels. All branch and production text is shown in that screen, including abilities that aren't built, so `BranchAbilityEvent` and the other handlers hold the real list of working abilities.

Vanilla enchanting tables, brewing stands, and anvils are blocked for everyone in `ClassRestrictionEvent`, because the mod's workstations replace them.

`api/` separates gear options and potions from classes. Workstations should check `ProductionAction.canUse` rather than reading the class, and new content goes in `GearOptionRegistry` or `AlchemyEffectRegistry`. `GearData` reads and writes gear NBT. None of the workstations exist yet.

### Leveled monsters

`CustomZombieEntity` implements `LeveledMob`. In `finalizeSpawn` it copies the nearest player's level and adds permanent health and damage modifiers, so a monster keeps its level after that player leaves. Natural spawning needs both a placement in `EntityAttributeEvent` and a JSON in `data/alpgmc/forge/biome_modifier/`. Drops come from `data/alpgmc/loot_tables/entities/`.

### Adding things

- **Entity:** register it in `EntityInit`, add attributes in `EntityAttributeEvent`, add a renderer in `ClientEntityEvent`, add its name to both lang files (`en_us.json`, `ko_kr.json`), and add an XP entry in `mob_xp.json`.
- **Packet:** add a `messageBuilder` call in `PacketHandler.register()`.
- **Items:** `WeaponCategory` and `ArmorCategory` each map to a `MainCategory`, but nothing uses them yet. `WeaponRegistry` is empty, and the `ItemInit` call in `ExampleMod` is commented out.

### Build details

- `processResources` expands `${...}` properties from `gradle.properties` into `META-INF/mods.toml` and `pack.mcmeta`, so a literal `${` in those files breaks the build.
- `build.gradle` declares no mod dependencies, only Forge itself. The README's dependency list (Apotheosis, Dimensional Dungeons, Project MMO, KubeJS, FTB Quests) is not in the dev environment.

## Conventions

- Code comments are written in Korean, and player-facing text is localized in both `en_us.json` and `ko_kr.json`.
- The package is `com.sxxxp.ALPGMC` (uppercase), while `mod_group_id` is `com.sxxxp.alpgmc`. The repo folder is named `ALPOEMC`.
- Copyright is All Rights Reserved; see the README for the modpack terms.
