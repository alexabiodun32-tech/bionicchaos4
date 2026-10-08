# Bionic's Ultimate Chaos (Minecraft 1.21.1 / NeoForge)

Original mod, not affiliated with or copied from any YouTuber's assets.

## Build the .jar and .mrpack
Needs internet on first build (downloads Gradle, NeoForge and Minecraft). The Gradle wrapper is included, so you only need Java.

1. Install JDK 21 (e.g. Temurin). Or open the folder in IntelliJ IDEA, which imports Gradle projects itself.
2. In this folder run `./gradlew build` (Windows: `gradlew.bat build`).
3. Your mod is at `build/libs/bionicchaos-1.0.0.jar`. Drop it in `.minecraft/mods` with NeoForge 21.1.x installed.
4. Optional modpack: `python3 tools/make_mrpack.py` makes `build/bionicchaos-1.0.0.mrpack`.

Test without installing: `./gradlew runClient`

If Gradle says the NeoForge version doesn't exist, change `version` in the `neoForge {}` block of build.gradle to any 21.1.x from https://projects.neoforged.net/neoforged/neoforge

## What's in 1.0.0
Craft a **Power Core** (iron/redstone ring around a diamond), then combine it with extra items to make a core.
Hold a core in either hand for passive powers; right-click for active ones.

| Core | Power |
|---|---|
| Speed | Super speed |
| Jump | Super jump + no fall damage |
| Flight | Creative-style flight while held |
| Quantum | Flight + sneak/right-click teleport (32 blocks) |
| Strength | Super strength |
| Regen | Fast regeneration |
| Stealth | Invisibility |
| Flame | Fire immunity |
| Aqua | Water breathing |
| Scanner | Right-click: finds nearest ore within 24 blocks |
| Shockwave | Right-click: damages and blasts back nearby mobs |
| Chrono | Right-click: slows mobs around you, speeds you up |
| Ultimate | Speed, jump, flight, strength, regen, fire, water. Right-click shockwave, sneak + right-click teleport |
| **Bionic Super Core** | Works from anywhere in your inventory. Every power (except invisibility), Speed V, Strength X, Resistance V, flight. Right-click shockwave, sneak + right-click teleport |

**Bionic Super Sword**: 1,000 damage per hit, lightning on every strike, unbreakable, fire resistance while held.
Craft: Ultimate + Scanner + Chrono + Stealth core + nether star -> Super Core; netherite sword + Super Core + lightning rod + diamond block -> Super Sword.
Commands: `/give @p bionicchaos:bionic_super_core` and `/give @p bionicchaos:bionic_super_sword`

Everything shows in the "Bionic's Ultimate Chaos" creative tab.

## Roadmap (not built yet)
Vehicles, mobs/bosses, structures (labs, bunkers, crash sites), custom machines.

## Commands (need operator / cheats)
| Command | Does |
|---|---|
| `/bionic give super_core` | Gives the Bionic Super Core |
| `/bionic give super_sword` | Gives the Bionic Super Sword |
| `/bionic give all` | Both |
| `/bionic god` | Toggle invulnerability |
| `/bionic fly` | Toggle flight |
| `/bionic heal` | Full health and food |
| `/bionic lightning` | Lightning where you look (100 blocks) |
| `/bionic shockwave` | Shockwave around you |

## Publishing on Modrinth
1. Build the jar (`./gradlew build`) and test it in a real 1.21.1 NeoForge world first.
2. modrinth.com -> Create project -> type Mod, add name, summary, description, icon (`src/main/resources/logo.png`).
3. Pick license MIT (see LICENSE), client + server side "required".
4. Create a version: upload `build/libs/bionicchaos-1.0.0.jar`, loader NeoForge, game version 1.21.1, release channel.
5. Submit for review. Keep the mod original: no copied assets, no impersonation of another creator.

## No Java installed? Build it online
Push this folder to a GitHub repo. The included workflow (`.github/workflows/build.yml`) builds it on GitHub's servers.
Open the repo's Actions tab, click the latest run, and download the two artifacts at the bottom of the page:
`bionicchaos-jar` (contains the .jar) and `bionicchaos-mrpack` (contains the .mrpack). GitHub wraps each in a .zip, so unzip them.
To get a proper download page, push a tag (e.g. `git tag v1.0.0 && git push --tags`) and both files are attached to a GitHub Release.

Uploading through the GitHub website? Folders starting with a dot (`.github`) are often hidden on your computer, and the upload page can skip them.
If the Actions tab shows nothing, create the file `.github/workflows/build.yml` on the GitHub website (Add file -> Create new file) and paste in its contents.

## Modrinth file types
- Mod -> upload the `.jar` (this project). Loader NeoForge, game version 1.21.1.
- Modpack -> `.mrpack`. After building, run `python3 tools/make_mrpack.py` to get `build/bionicchaos-1.0.0.mrpack` with the mod bundled.
