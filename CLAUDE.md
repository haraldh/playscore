# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android development environment project using Nix flakes. It provides a reproducible development environment for Android app development with Kotlin and Java.

## Key Commands

### Environment Setup
```bash
# Enter the development environment (if not using direnv)
nix develop

# The environment automatically sets up:
# - ANDROID_HOME and ANDROID_SDK_ROOT
# - JAVA_HOME (JDK 17)
# - PATH with all Android SDK tools
```

### Android Development Commands
```bash
# Accept Android SDK licenses (required on first setup)
yes | sdkmanager --licenses

# Build an Android project
./gradlew build

# Run tests
./gradlew test

# Clean build artifacts
./gradlew clean

# Connect to device/emulator
adb devices

# Start Android emulator
emulator -list-avds  # List available AVDs
emulator -avd <name> # Start specific AVD

# Mirror device screen
scrcpy

# Lint Kotlin code
ktlint
```

## Architecture & Structure

### Development Environment Configuration
- **flake.nix**: Core Nix flake configuration that defines:
  - Android SDK components (build tools 35.0.0, platform 36)
  - Development tools (JDK 17, Gradle, Maven, Kotlin, CMake, Ninja)
  - Android tools (adb, emulator, scrcpy)
  - Environment variables and PATH setup

- **.envrc**: Enables automatic environment loading with direnv

### Android SDK Components
- Platform version: Android 36
- Build tools: 35.0.0
- ABIs: x86_64 and arm64-v8a
- NDK: version 25.2.9519653
- Extras: Google Play services, Google repository

### Development Tools Available
- **Java/Kotlin**: JDK 17, Kotlin compiler, Kotlin language server
- **Build Tools**: Gradle, Maven, CMake, Ninja
- **Android Tools**: adb, sdkmanager, emulator, scrcpy
- **Code Quality**: ktlint
- **Version Control**: git

## Projects in this Repository

### playscore/
A game score-keeping Android app for family & friends game evenings. See the root `README.md` and `playscore/SPEC.md` for details.

**Tech Stack:**
- Jetpack Compose with Material 3
- Room database for persistence
- MVVM architecture with ViewModels
- Navigation Compose

**Key commands:**
```bash
cd playscore
./gradlew build           # Build the app
./gradlew installDebug    # Install on device/emulator
./gradlew clean          # Clean build artifacts
```

**App Structure:**
- `data/local/` - Room database, DAOs, entities (Player, Game, GameParticipant)
- `data/repository/` - PlayerRepository, GameRepository
- `ui/players/` - Player selection screen
- `ui/game/` - Active game with score tracking
- `ui/results/` - Final standings with winner
- `ui/history/` - Past games browser

## Important Notes

- Android Studio is available but commented out in flake.nix (uncomment `android-studio` if needed)
- First-time setup requires accepting Android SDK licenses
- The environment supports both x86_64 and ARM64 architectures
- All tools are pinned to specific versions via flake.lock for reproducibility
- This project uses release-please github actions.