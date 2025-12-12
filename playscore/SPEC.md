# PlayScore - Game Score Keeper

A simple Android app to keep score during family & friends game evenings.

## Features

### Player Management
- Maintain a list of "frequent players" (persisted across sessions)
- Select which players participate in current game
- Add new players at any time
- Remove obsolete players from the frequent list

### Game Session
- All participants start with score of 0
- Display all players with their current scores
- Sorting options (user-selectable):
  - By score (highest first)
  - By score (lowest first)
  - Alphabetically by name
  - Original order (order added to game)
- Quick "+1" button for each player
- Custom score entry: add any positive or negative value
- End game button (with confirmation)
- Minimum 1 player required to start a game

### Results & History
- Final standings screen showing winner and all rankings
- Tie handling: players with equal scores share the same rank
- Games automatically saved to history
- History view: browse past games with dates, participants, and scores

## Technical Specifications

- **Package**: org.surfsite.playscore
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Database**: Room (local persistence)
- **Architecture**: MVVM with ViewModels and StateFlow
- **Navigation**: Navigation Compose

## Screens

1. **Players Screen** (Start) - Manage frequent players, select participants, start game
2. **Game Screen** - Active game with score tracking and sorting options
3. **Results Screen** - Final standings with winner highlighted
4. **History Screen** - Browse past games

## Data Models

### Player
- ID (auto-generated)
- Name
- Created timestamp

### Game
- ID (auto-generated)
- Started timestamp
- Ended timestamp (nullable)
- Active flag

### GameParticipant
- Game ID
- Player ID
- Score
- Display order (for original order sorting)
