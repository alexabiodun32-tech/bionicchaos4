#!/usr/bin/env python3
"""Package the built jar into a Modrinth modpack (.mrpack) for NeoForge 1.21.1.

Run AFTER building the jar (from anywhere):
    ./gradlew build
    python3 tools/make_mrpack.py

Output: build/bionicchaos-<version>.mrpack, importable in the Modrinth App,
Prism Launcher and other launchers that read .mrpack files.
"""
import json
import pathlib
import re
import sys
import zipfile

ROOT = pathlib.Path(__file__).resolve().parent.parent
BUILD = ROOT / "build"
LIBS = BUILD / "libs"

MINECRAFT_VERSION = "1.21.1"


def read_build_gradle():
    """Pull the mod version and NeoForge version from build.gradle so they never drift."""
    text = (ROOT / "build.gradle").read_text(encoding="utf-8")
    mod_version = re.search(r"^version\s*=\s*['\"]([^'\"]+)['\"]", text, re.M)
    neo_version = re.search(r"neoForge\s*\{[^}]*?version\s*=\s*['\"]([^'\"]+)['\"]", text, re.S)
    if not mod_version or not neo_version:
        sys.exit("Could not read version / neoForge version from build.gradle.")
    return mod_version.group(1), neo_version.group(1)


def main():
    version, neoforge = read_build_gradle()

    jar = LIBS / f"bionicchaos-{version}.jar"
    if not jar.is_file():
        candidates = [p for p in LIBS.glob("bionicchaos-*.jar")
                      if not p.name.endswith(("-sources.jar", "-javadoc.jar"))]
        if not candidates:
            sys.exit("No jar found in build/libs - run `./gradlew build` first.")
        jar = max(candidates, key=lambda p: p.stat().st_mtime)

    index = {
        "formatVersion": 1,
        "game": "minecraft",
        "versionId": version,
        "name": "Ultimate Chaos Pack",
        "summary": f"NeoForge {MINECRAFT_VERSION} with Bionic's Ultimate Chaos pre-installed.",
        "files": [],
        "dependencies": {"minecraft": MINECRAFT_VERSION, "neoforge": neoforge},
    }

    out = BUILD / f"bionicchaos-{version}.mrpack"
    with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as z:
        z.writestr("modrinth.index.json", json.dumps(index, indent=2))
        z.write(jar, "overrides/mods/" + jar.name)

    print(f"Created {out.relative_to(ROOT)}  (mod: {jar.name}, NeoForge {neoforge})")


if __name__ == "__main__":
    main()
